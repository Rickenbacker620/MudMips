package mylib

import spinal.core._


object InstPattern {
  //------------------------------------
  //指令全码
  //|==================================================|
  //|31----26|25----21|20----16|15----11|10----6|5----0|
  //|___OP___|___RS___|___RT___|___RD___|___SA__|_FUNC_|-----R类型-----
  //|==================================================|
  //|31----26|25----21|20----16|15--------------------0|
  //|___OP___|___RS___|___RT___|______IMMEDIATE________|-----I类型-----
  //|==================================================|
  //|31----26|25--------------------------------------0|
  //|___OP___|_________________ADDRESS_________________|-----J类型-----
  //|==================================================|

  //  def R_TYPE = M"------_-----_-----_-----_-----_------"
  //  def I_TYPE = M"------_-----_-----_----------------"

  def LUI = M"001111_00000_-----_----------------"

  def ORI = M"001101_-----_-----_----------------"

  def ADDU = M"000000_-----_-----_-----_00000_100001"

  def BNE = M"000101_-----_-----_----------------"

  def LW = M"100011_-----_-----_----------------"

  def SW = M"101011_-----_-----_----------------"

  def RS = 25 downto 21

  def RT = 20 downto 16

  def RD = 15 downto 11

  def SA = 10 downto 6

  def IMM = 15 downto 0

  def SHIFT = 4 downto 0
}

object AluOp extends SpinalEnum(binarySequential){
  val OR_OP, AND_OP, LUI_OP,
  SLL_OP, SRL_OP, SRA_OP, ADD_OP, NOP_OP,
  JAL_OP, BNE_OP, LW_OP, SW_OP = newElement()
}

object AluSel extends SpinalEnum(binarySequential) {
  val RES_LOGIC, RES_SHIFT, RES_ARITH, RES_NOP, RES_JUMP, RES_LOAD_STORE = newElement()
}

//object AluOp extends SpinalEnum {
//  val OR_OP, AND_OP, XOR_OP, NOR_OP, LUI_OP,
//  SLL_OP, SRL_OP, SRA_OP, ADD_OP, NOP_OP,
//  MFHI_OP, MFLO_OP, MTHI_OP, MTLO_OP, MOVN_OP,
//  MOVZ_OP, JAL_OP, BEQ_OP, LW_OP, LB_OP, SB_OP, SW_OP = newElement()
//}
//
//object AluSel extends SpinalEnum {
//  val RES_LOGIC, RES_SHIFT, RES_ARITH, RES_NOP, RES_JUMP = newElement()
//}
