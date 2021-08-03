package mylib

import spinal.core._
import spinal.lib._

object MipsInsts {
  val placeholders = Map(
    "RS" -> "-----",
    "RT" -> "-----",
    "RD" -> "-----",
    "BASE" -> "-----",
    "SHAMT" -> "-----",
    "IMM" -> "----------------",
    "OFFSET" -> "----------------"
  )
  val instList = List(

    ImmCalcModel("LUI", "001111_00000_RT_IMM", AluOp.OR_OP, AluSel.RES_LOGIC),

    ImmCalcModel("ORI", "001101_RS_RT_IMM", AluOp.OR_OP, AluSel.RES_LOGIC)

//  "ADDU" -> InstModelBase1("000000_RS_RT_RD_00000_100001")
//  ,
//
//  "BNE" -> InstModelBase1("000101_RS_RT_OFFSET")
//  ,
//
//  "LW" -> InstModelBase1("100011_BASE_RT_OFFSET")
//  ,
//
//  "SW" -> InstModelBase1("101011_BASE_RT_OFFSET")
  )
}


object InstKind extends Enumeration {
  type InstKind = Value
  val I_TYPE, J_TYPE, R_TYPE = Value
}

object RegKind extends Enumeration {
  type RegKind = Value
  val RS2RT, RSRT2RD, RS, RT, NONE = Value
}

object ImmKind extends Enumeration {
  type ImmKind = Value
  val SIGN, UNSIGN, NONE = Value
}

abstract class InstModelBase(name: String, pattern: String) {
  private val rawStr = pattern
  val instName = name
  private val maskStr: String = MipsInsts.placeholders.foldLeft(rawStr)((str, pair) => {
    str.replace(pair._1, pair._2)
  })
  //  def RS = 25 downto 21
  //
  //  def RT = 20 downto 16
  //
  //  def RD = 15 downto 11
  //
  //  def SA = 10 downto 6
  //
  //  def IMM = 15 downto 0
  //
  //  def SHIFT = 4 downto 0

  val maskPattern = MaskedLiteral(maskStr)

  def GenCircuit(IDPorts: IDPorts): Area

  def DecodeInst(inst: Bits) = {
    object parts {
      val RS = inst(25 downto 21).asUInt
      val BASE = inst(25 downto 21).asUInt
      val RT = inst(20 downto 16).asUInt
      val RD = inst(15 downto 11).asUInt
      val SHAMT = inst(10 downto 6).asUInt
      val SIMM = inst(15 downto 0).asSInt.resize(32).asBits
      val UIMM = inst(15 downto 0).asUInt.resize(32).asBits
      val IMM = inst(15 downto 0)
      val SHIFT = inst(4 downto 0).asUInt
    }

    parts
  }
}

case class ImmCalcModel(name: String, pattern: String, aluOp: AluOp.E, aluSel: AluSel.E) extends InstModelBase(name, pattern) {
  override def GenCircuit(io: IDPorts): Area = new Area {
    val instParts = DecodeInst(io.inst)
    io.rregs(0).en := True
    io.rregs(0).addr := instParts.RS

    io.rregs(1).en := False
    io.rregs(1).addr := 0

    io.oprd(0) := io.rregs(0).data
    if (instName == "LUI") {
      io.oprd(1) := (instParts.IMM ## B"16'b0").asBits
    }
    else {
      io.oprd(1) := instParts.UIMM
    }

    io.alu.op := aluOp
    io.alu.sel := aluSel

    io.wreg.en := True
    io.wreg.addr := instParts.RT
  }
}


case class RegCalcModel(name: String, pattern: String, aluOp: AluOp.E, aluSel: AluSel.E) extends InstModelBase(name, pattern) {
  override def GenCircuit(io: IDPorts): Area = new Area {
    val instParts = DecodeInst(io.inst)
    io.rregs(0).en := True
    io.rregs(0).addr := instParts.RS

    io.rregs(1).en := False
    io.rregs(1).addr := 0

    io.oprd(0) := io.rregs(0).data
    io.oprd(1) := io.rregs(1).data

    io.alu.op := aluOp
    io.alu.sel := aluSel

    io.wreg.en := True
    io.wreg.addr := instParts.RD
  }
}


case class RegFiles() extends Component {
  val io = new Bundle {
    val wreg = in(new RegFile)
    val rregs = Vec(slave(new RegFile), 2)
  }

  val regs = Vec(RegInit(B"32'b0"), 32)

  val readReg = new Area {
    io.rregs.foreach(rreg => {
      when(rreg.en) {
        rreg.data := regs(rreg.addr)
      }.otherwise {
        rreg.data := 0
      }
    })
  }

  val writereg = new Area {
    val wreg = io.wreg
    when(wreg.en) {
      regs(wreg.addr) := wreg.data
    }
  }
}

class IDPorts extends Bundle {
  val inst = in(Bits(32 bits))
  val alu = out(Alu())
  val rregs = Vec(master(RegFile()), 2)
  val wreg = master(RegFile())
  val oprd = out(Vec(Bits(32 bits), 2))
}

case class ID() extends Component {
  val io = new IDPorts
  switch(io.inst) {
    for (inst <- MipsInsts.instList) {
      is(inst.maskPattern) {
        inst.GenCircuit(io)
      }
      default {
        inst.GenCircuit(io)
      }
    }
  }
}
