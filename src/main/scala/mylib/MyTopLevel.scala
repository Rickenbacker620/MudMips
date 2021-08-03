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

import scala.util.Random


//Hardware definition
trait FFIO {
  val io: Bundle
}


case class PCI() extends Component with FFIO {
  val io = new Bundle {
    val ce = in Bool()
    val pc = in UInt (32 bits)
  }

  val regt = RegInit(False)
  val pcpc = RegInit(U"32'b0")
  regt := io.ce
  pcpc := io.pc
}

case class Opcode() extends SpinalEnum {
}

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
    val cond1 = in Bool
    val flag = out Bool
  }

  io.flag := io.cond1

  val pc1 = new PC
  val pc2 = new PCI

  //  pc1.io <> pc2.io
  println(pc1.io.getComponents())
  println(pc2.io)
  pc2.io := RegNext(pc2.io)
  val midw = new MiddleWare(pc1.io, pc2.io)
  //  midw.io.in <> pc1.io
  //  midw.io.out <> pc2.io
}

//Generate the MyTopLevel's Verilog
object MyTopLevelVerilog {
  def main(args: Array[String]) {
    MySpinalConfig.generateVerilog(new ID).printPruned()
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
    move(1,2)
  }

  override def toString =
    s"($x, $y)"
}


object LetsTest {
  def main(args: Array[String]): Unit = {
    val horay = new Point(10,21)
    horay.moveone()
  }
}
