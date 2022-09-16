package blinky
import chisel3._
import chisel3.experimental.ChiselEnum
import chisel3.util._

object Color extends ChiselEnum{
  val Black   = Value("b000".U(3.W))
  val Red0    = Value("b001".U(3.W))
  val Blue    = Value("b010".U(3.W))
  val Red1    = Value("b011".U(3.W))
  val Green   = Value("b100".U(3.W))
  val Red2    = Value("b101".U(3.W))
  val White   = Value("b110".U(3.W))
  val Red3    = Value("b111".U(3.W))
}

object LedState extends ChiselEnum{
  val State0 = Value("b0000000".U)
  val State1 = Value("b0001000".U)
  val State2 = Value("b0011100".U)
  val State3 = Value("b0111110".U)
  val State4 = Value("b1111111".U)
}



class Blinky extends Module {
  val io = IO(new Bundle {
    val  led_rgb_multiplex_a = Output (UInt (7.W))
    val led_rgb_multiplex_b = Output(UInt(3.W))
  })
  val led_rgb_multiplex_aReg = Reg (UInt(7.W))
  val led_rgb_multiplex_bReg = Reg(UInt(3.W))
  val countOn = true.B
  val rgbCountOn = true.B
  val rgbStateCounter = RegInit(0.U(3.W))
  val (counterValue,counterWrap) = Counter(countOn,30000000)
  val (rgbCounterValue,rgbCounterWrap) = Counter(rgbCountOn,240000000)


  io.led_rgb_multiplex_a := led_rgb_multiplex_aReg
  io.led_rgb_multiplex_b := led_rgb_multiplex_bReg

  when(counterWrap){
    led_rgb_multiplex_aReg := ~led_rgb_multiplex_aReg
  }

  when(rgbCounterWrap){
    rgbStateCounter := rgbStateCounter + 1.U
  }

  switch(rgbStateCounter){
    is(0.U){
      led_rgb_multiplex_bReg :=  "b001".U
    }
    is(1.U){
      led_rgb_multiplex_bReg :=  "b100".U
    }
    is(2.U){
      led_rgb_multiplex_bReg :=  "b010".U

    }
    is(3.U) {
      led_rgb_multiplex_bReg := "b110".U
    }
    is(4.U){
      rgbStateCounter := 0.U
    }
  }

}

object BlinkyObject extends App {
  emitVerilog(new Blinky(), Array("--target-dir", "generated"))
}