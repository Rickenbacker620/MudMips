package mylib

import spinal.core._
import spinal.lib._

class WB extends Component {
  val io = new Bundle {
    val wreg_in = in(RegFile())
    val wreg_out = out(RegFile())
  }

  io.wreg_out := io.wreg_in
  
}


