package mylib
import spinal.core._


class Stage[T <: Bundle](gen: => T) extends Component {
  val left: T = gen.flip()
  val right = createOutPort(left)

  def createOutPort(inBundle: Bundle) = new Bundle {
    for (i <- inBundle.elements) {
      val a = out(Reg(i._2.clone()))
      a match {
        case s: Bits => s.init(0)
        case b: Bool => b.init(False)
      }
      valCallbackRec(a, i._1)
      a := i._2
    }
  }
}


case class PC() extends Component {
  val io = new Bundle {
    val ce = out Bool()
    val pc = out UInt (32 bits)
  }

  val ce = RegInit(False)
  val pc = RegInit(U"32'h0")

  io.ce := ce
  io.pc := pc

  ce := True

  when(!ce) {
    pc := 0
  }.otherwise {
    pc := pc + 4
  }
}
