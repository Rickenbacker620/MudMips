/*
 * SpinalHDL
 * Copyright (c) Dolu, All rights reserved.
 *
 * This library is free software; you can redistribute it and/or
 * modify it under the terms of the GNU Lesser General Public
 * License as published by the Free Software Foundation; either
 * version 3.0 of the License, or (at your option) any later version.
 *
 * This library is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
 * Lesser General Public License for more details.
 *
 * You should have received a copy of the GNU Lesser General Public
 * License along with this library.
 */

package mylib

import spinal.core._
import spinal.lib._

case class Alu() extends Bundle {
  val op = AluOp()
  val sel = AluSel()
}


case class RegFile() extends Bundle with IMasterSlave {
  val en = Bool()
  val addr = UInt(5 bits)
  val data = Bits(32 bits)

  override def asMaster() {
    out(en, addr)
    in(data)
  }
}

case class MiddleWare(bundle1: Bundle, bundle2: Bundle) extends Component {
  val io = new Bundle {
    val in = bundle1
    val out = bundle2
  }
  println(io.in)
  println(io.out)
  val horay = new Alu {


  }
  //  io.in <> io.out
}


class MyTopLevel extends Component {
  val io = new Bundle {
    val rom_ce = out Bool()
    val rom_addr = out UInt (32 bits)
    val rom_data = in Bits(32 bits)
  }
  val pc = new PC
  val id = new ID
  val ex = new EX
  val regfiles=  new RegFiles
  val mem = new MEM
//  val wb = new WB

  io.rom_ce := pc.io.ce
  io.rom_addr := pc.io.pc
  id.io.inst := io.rom_data
  id.io.rregs <> regfiles.io.rregs
  id.io.oprd <> ex.io.oprd
  id.io.alu <> ex.io.alu
  id.io.wreg <> ex.io.wreg_in

  ex.io.wreg_out <> mem.io.wreg_in

  regfiles.io.wreg <> ex.io.wreg_out
}

//Generate the MyTopLevel's Verilog
object MyTopLevelVerilog {
  def main(args: Array[String]) {
    MySpinalConfig.generateVerilog(new MyTopLevel).printPruned()
  }
}

//Define a custom SpinalHDL configuration with synchronous reset instead of the default asynchronous one. This configuration can be resued everywhere
object MySpinalConfig extends SpinalConfig(
  defaultConfigForClockDomains = ClockDomainConfig(resetKind = SYNC)
)

class Point(var x: Int, var y: Int) {

  def move(dx: Int, dy: Int): Unit = {
    x = x + x
    y = y + dy
  }

  def moveone() {
    move(1, 2)
  }

  override def toString =
    s"($x, $y)"
}


object LetsTest {
  def main(args: Array[String]): Unit = {
    val horay = new Point(10, 21)
    horay.moveone()
  }
}
