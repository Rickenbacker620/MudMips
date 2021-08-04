package mylib

import spinal.core._
import spinal.lib._

class EX extends Component {
  val io = new Bundle {
    val alu = in(Alu())
    val wreg_in = in(RegFile())
    val oprd = in(Vec(Bits(32 bits), 2))
    val wreg_out = out(RegFile())
  }

  val logicRes = Bits(32 bits)
  val shiftRes = Bits(32 bits)
  val arithRes = Bits(32 bits)
  val moveRes = Bits(32 bits)

  io.wreg_out.addr := io.wreg_in.addr
  io.wreg_out.en := io.wreg_in.en


  switch(io.alu.op) {
    is(AluOp.OR_OP) {
      logicRes := io.oprd(0) | io.oprd(1)
    }
    is(AluOp.AND_OP) {
      logicRes := io.oprd(0) & io.oprd(1)
    }
    default {
      logicRes := 0
    }
  }

  switch(io.alu.sel) {
    is(AluSel.RES_LOGIC) {
      io.wreg_out.data := logicRes
    }
    default {
      io.wreg_out.data := 0
    }
  }

}
