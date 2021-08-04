package mylib

import spinal.core._
import spinal.lib._

class MEM extends Component {
  val io = new Bundle {
//    val alu = in(Alu())
    val wreg_in = in(RegFile())
    val wreg_out = out(RegFile()) setAsReg()
  }

  io.wreg_out.en.init(False)
  io.wreg_out.data.init(0)
  io.wreg_out.addr.init(0)

  io.wreg_out := io.wreg_in
}
