/*****************************************************************************
 *
 *	 Portable uPD7810/11, 7810H/11H, 78C10/C11/C14 emulator V0.2
 *	 Copyright (c) 2001 Juergen Buchmueller, all rights reserved.
 *
 *	 7810tbl.c	- function pointer tables
 *
 *****************************************************************************/

static static 
static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static static 
static struct opcode_s op48[256] =
{
	{illegal,		2, 8, 8,L0|L1}, /* 00: 0100 1000 0000 0000						*/
	{SLRC_A,		2, 8, 8,L0|L1}, /* 01: 0100 1000 0000 0001						*/
	{SLRC_B,		2, 8, 8,L0|L1}, /* 02: 0100 1000 0000 0010						*/
	{SLRC_C,		2, 8, 8,L0|L1}, /* 03: 0100 1000 0000 0011						*/
	{illegal,		2, 8, 8,L0|L1}, /* 04: 0100 1000 0000 0100						*/
	{SLLC_A,		2, 8, 8,L0|L1}, /* 05: 0100 1000 0000 0101						*/
	{SLLC_B,		2, 8, 8,L0|L1}, /* 06: 0100 1000 0000 0110						*/
	{SLLC_C,		2, 8, 8,L0|L1}, /* 07: 0100 1000 0000 0111						*/
	{SK_NV, 		2, 8, 8,L0|L1}, /* 08: 0100 1000 0000 1000						*/
	{illegal,		2, 8, 8,L0|L1}, /* 09: 0100 1000 0000 1001						*/
	{SK_CY, 		2, 8, 8,L0|L1}, /* 0a: 0100 1000 0000 1010						*/
	{SK_HC, 		2, 8, 8,L0|L1}, /* 0b: 0100 1000 0000 1011						*/
	{SK_Z,			2, 8, 8,L0|L1}, /* 0c: 0100 1000 0000 1100						*/
	{illegal,		2, 8, 8,L0|L1}, /* 0d: 0100 1000 0000 1101						*/
	{illegal,		2, 8, 8,L0|L1}, /* 0e: 0100 1000 0000 1110						*/
	{illegal,		2, 8, 8,L0|L1}, /* 0f: 0100 1000 0000 1111						*/

	{illegal2,		2, 8, 8,L0|L1}, /* 10: 0100 1000 0001 0000						*/
	{illegal2,		2, 8, 8,L0|L1}, /* 11: 0100 1000 0001 0001						*/
	{illegal2,		2, 8, 8,L0|L1}, /* 12: 0100 1000 0001 0010						*/
	{illegal2,		2, 8, 8,L0|L1}, /* 13: 0100 1000 0001 0011						*/
	{illegal2,		2, 8, 8,L0|L1}, /* 14: 0100 1000 0001 0100						*/
	{illegal2,		2, 8, 8,L0|L1}, /* 15: 0100 1000 0001 0101						*/
	{illegal2,		2, 8, 8,L0|L1}, /* 16: 0100 1000 0001 0110						*/
	{illegal2,		2, 8, 8,L0|L1}, /* 17: 0100 1000 0001 0111						*/
	{SKN_NV,		2, 8, 8,L0|L1}, /* 18: 0100 1000 0001 1000						*/
	{illegal,		2, 8, 8,L0|L1}, /* 19: 0100 1000 0001 1001						*/
	{SKN_CY,		2, 8, 8,L0|L1}, /* 1a: 0100 1000 0001 1010						*/
	{SKN_HC,		2, 8, 8,L0|L1}, /* 1b: 0100 1000 0001 1011						*/
	{SKN_Z, 		2, 8, 8,L0|L1}, /* 1c: 0100 1000 0001 1100						*/
	{illegal,		2, 8, 8,L0|L1}, /* 1d: 0100 1000 0001 1101						*/
	{illegal,		2, 8, 8,L0|L1}, /* 1e: 0100 1000 0001 1110						*/
	{illegal,		2, 8, 8,L0|L1}, /* 1f: 0100 1000 0001 1111						*/

	{illegal,		2, 8, 8,L0|L1}, /* 20: 0100 1000 0010 0000						*/
	{SLR_A, 		2, 8, 8,L0|L1}, /* 21: 0100 1000 0010 0001						*/
	{SLR_B, 		2, 8, 8,L0|L1}, /* 22: 0100 1000 0010 0010						*/
	{SLR_C, 		2, 8, 8,L0|L1}, /* 23: 0100 1000 0010 0011						*/
	{illegal,		2, 8, 8,L0|L1}, /* 24: 0100 1000 0010 0100						*/
	{SLL_A, 		2, 8, 8,L0|L1}, /* 25: 0100 1000 0010 0101						*/
	{SLL_B, 		2, 8, 8,L0|L1}, /* 26: 0100 1000 0010 0110						*/
	{SLL_C, 		2, 8, 8,L0|L1}, /* 27: 0100 1000 0010 0111						*/
	{JEA,			2, 8, 8,L0|L1}, /* 28: 0100 1000 0010 1000						*/
	{CALB,			2,17,17,L0|L1}, /* 29: 0100 1000 0010 1001						*/
	{CLC,			2, 8, 8,L0|L1}, /* 2a: 0100 1000 0010 1010						*/
	{STC,			2, 8, 8,L0|L1}, /* 2b: 0100 1000 0010 1011						*/
	{illegal,		2,32,32,L0|L1}, /* 2c: 0100 1000 0010 1100						*/
	{MUL_A, 		2,32,32,L0|L1}, /* 2d: 0100 1000 0010 1101						*/
	{MUL_B, 		2,32,32,L0|L1}, /* 2e: 0100 1000 0010 1110						*/
	{MUL_C, 		2,32,32,L0|L1}, /* 2f: 0100 1000 0010 1111						*/

	{illegal,		2, 8, 8,L0|L1}, /* 30: 0100 1000 0011 0000						*/
	{RLR_A, 		2, 8, 8,L0|L1}, /* 31: 0100 1000 0011 0001						*/
	{RLR_B, 		2, 8, 8,L0|L1}, /* 32: 0100 1000 0011 0010						*/
	{RLR_C, 		2, 8, 8,L0|L1}, /* 33: 0100 1000 0011 0011						*/
	{illegal,		2, 8, 8,L0|L1}, /* 34: 0100 1000 0011 0100						*/
	{RLL_A, 		2, 8, 8,L0|L1}, /* 35: 0100 1000 0011 0101						*/
	{RLL_B, 		2, 8, 8,L0|L1}, /* 36: 0100 1000 0011 0110						*/
	{RLL_C, 		2, 8, 8,L0|L1}, /* 37: 0100 1000 0011 0111						*/
	{RLD,			2,17,17,L0|L1}, /* 38: 0100 1000 0011 1000						*/
	{RRD,			2,17,17,L0|L1}, /* 39: 0100 1000 0011 1001						*/
	{NEGA,			2, 8, 8,L0|L1}, /* 3a: 0100 1000 0011 1010						*/
	{HALT,			2,12,12,L0|L1}, /* 3b: 0100 1000 0011 1011						*/
	{illegal,		2, 8, 8,L0|L1}, /* 3c: 0100 1000 0011 1100						*/
	{DIV_A, 		2,59,59,L0|L1}, /* 3d: 0100 1000 0011 1101						*/
	{DIV_B, 		2,59,59,L0|L1}, /* 3e: 0100 1000 0011 1110						*/
	{DIV_C, 		2,59,59,L0|L1}, /* 3f: 0100 1000 0011 1111						*/

	{SKIT_NMI,		2, 8, 8,L0|L1}, /* 40: 0100 1000 0100 0000						*/
	{SKIT_FT0,		2, 8, 8,L0|L1}, /* 41: 0100 1000 0100 0001						*/
	{SKIT_FT1,		2, 8, 8,L0|L1}, /* 42: 0100 1000 0100 0010						*/
	{SKIT_F1,		2, 8, 8,L0|L1}, /* 43: 0100 1000 0100 0011						*/
	{SKIT_F2,		2, 8, 8,L0|L1}, /* 44: 0100 1000 0100 0100						*/
	{SKIT_FE0,		2, 8, 8,L0|L1}, /* 45: 0100 1000 0100 0101						*/
	{SKIT_FE1,		2, 8, 8,L0|L1}, /* 46: 0100 1000 0100 0110						*/
	{SKIT_FEIN, 	2, 8, 8,L0|L1}, /* 47: 0100 1000 0100 0111						*/
	{SKIT_FAD,		2, 8, 8,L0|L1}, /* 48: 0100 1000 0100 1000						*/
	{SKIT_FSR,		2, 8, 8,L0|L1}, /* 49: 0100 1000 0100 1001						*/
	{SKIT_FST,		2, 8, 8,L0|L1}, /* 4a: 0100 1000 0100 1010						*/
	{SKIT_ER,		2, 8, 8,L0|L1}, /* 4b: 0100 1000 0100 1011						*/
	{SKIT_OV,		2, 8, 8,L0|L1}, /* 4c: 0100 1000 0100 1100						*/
	{illegal2,		2, 8, 8,L0|L1}, /* 4d: 0100 1000 0100 1101						*/
	{illegal2,		2, 8, 8,L0|L1}, /* 4e: 0100 1000 0100 1110						*/
	{illegal2,		2, 8, 8,L0|L1}, /* 4f: 0100 1000 0100 1111						*/

	{SKIT_AN4,		2, 8, 8,L0|L1}, /* 50: 0100 1000 0101 0000						*/
	{SKIT_AN5,		2, 8, 8,L0|L1}, /* 51: 0100 1000 0101 0001						*/
	{SKIT_AN6,		2, 8, 8,L0|L1}, /* 52: 0100 1000 0101 0010						*/
	{SKIT_AN7,		2, 8, 8,L0|L1}, /* 53: 0100 1000 0101 0011						*/
	{SKIT_SB,		2, 8, 8,L0|L1}, /* 54: 0100 1000 0101 0100						*/
	{illegal2,		2, 8, 8,L0|L1}, /* 55: 0100 1000 0101 0101						*/
	{illegal2,		2, 8, 8,L0|L1}, /* 56: 0100 1000 0101 0110						*/
	{illegal2,		2, 8, 8,L0|L1}, /* 57: 0100 1000 0101 0111						*/
	{illegal2,		2, 8, 8,L0|L1}, /* 58: 0100 1000 0101 1000						*/
	{illegal2,		2, 8, 8,L0|L1}, /* 59: 0100 1000 0101 1001						*/
	{illegal2,		2, 8, 8,L0|L1}, /* 5a: 0100 1000 0101 1010						*/
	{illegal2,		2, 8, 8,L0|L1}, /* 5b: 0100 1000 0101 1011						*/
	{illegal2,		2, 8, 8,L0|L1}, /* 5c: 0100 1000 0101 1100						*/
	{illegal2,		2, 8, 8,L0|L1}, /* 5d: 0100 1000 0101 1101						*/
	{illegal2,		2, 8, 8,L0|L1}, /* 5e: 0100 1000 0101 1110						*/
	{illegal2,		2, 8, 8,L0|L1}, /* 5f: 0100 1000 0101 1111						*/

	{SKNIT_NMI, 	2, 8, 8,L0|L1}, /* 60: 0100 1000 0110 0000						*/
	{SKNIT_FT0, 	2, 8, 8,L0|L1}, /* 61: 0100 1000 0110 0001						*/
	{SKNIT_FT1, 	2, 8, 8,L0|L1}, /* 62: 0100 1000 0110 0010						*/
	{SKNIT_F1,		2, 8, 8,L0|L1}, /* 63: 0100 1000 0110 0011						*/
	{SKNIT_F2,		2, 8, 8,L0|L1}, /* 64: 0100 1000 0110 0100						*/
	{SKNIT_FE0, 	2, 8, 8,L0|L1}, /* 65: 0100 1000 0110 0101						*/
	{SKNIT_FE1, 	2, 8, 8,L0|L1}, /* 66: 0100 1000 0110 0110						*/
	{SKNIT_FEIN,	2, 8, 8,L0|L1}, /* 67: 0100 1000 0110 0111						*/
	{SKNIT_FAD, 	2, 8, 8,L0|L1}, /* 68: 0100 1000 0110 1000						*/
	{SKNIT_FSR, 	2, 8, 8,L0|L1}, /* 69: 0100 1000 0110 1001						*/
	{SKNIT_FST, 	2, 8, 8,L0|L1}, /* 6a: 0100 1000 0110 1010						*/
	{SKNIT_ER,		2, 8, 8,L0|L1}, /* 6b: 0100 1000 0110 1011						*/
	{SKNIT_OV,		2, 8, 8,L0|L1}, /* 6c: 0100 1000 0110 1100						*/
	{illegal2,		2, 8, 8,L0|L1}, /* 6d: 0100 1000 0110 1101						*/
	{illegal2,		2, 8, 8,L0|L1}, /* 6e: 0100 1000 0110 1110						*/
	{illegal2,		2, 8, 8,L0|L1}, /* 6f: 0100 1000 0110 1111						*/

	{SKNIT_AN4, 	2, 8, 8,L0|L1}, /* 70: 0100 1000 0111 0000						*/
	{SKNIT_AN5, 	2, 8, 8,L0|L1}, /* 71: 0100 1000 0111 0001						*/
	{SKNIT_AN6, 	2, 8, 8,L0|L1}, /* 72: 0100 1000 0111 0010						*/
	{SKNIT_AN7, 	2, 8, 8,L0|L1}, /* 73: 0100 1000 0111 0011						*/
	{SKNIT_SB,		2, 8, 8,L0|L1}, /* 74: 0100 1000 0111 0100						*/
	{illegal2,		2, 8, 8,L0|L1}, /* 75: 0100 1000 0111 0101						*/
	{illegal2,		2, 8, 8,L0|L1}, /* 76: 0100 1000 0111 0110						*/
	{illegal2,		2, 8, 8,L0|L1}, /* 77: 0100 1000 0111 0111						*/
	{illegal2,		2, 8, 8,L0|L1}, /* 78: 0100 1000 0111 1000						*/
	{illegal2,		2, 8, 8,L0|L1}, /* 79: 0100 1000 0111 1001						*/
	{illegal2,		2, 8, 8,L0|L1}, /* 7a: 0100 1000 0111 1010						*/
	{illegal2,		2, 8, 8,L0|L1}, /* 7b: 0100 1000 0111 1011						*/
	{illegal2,		2, 8, 8,L0|L1}, /* 7c: 0100 1000 0111 1100						*/
	{illegal2,		2, 8, 8,L0|L1}, /* 7d: 0100 1000 0111 1101						*/
	{illegal2,		2, 8, 8,L0|L1}, /* 7e: 0100 1000 0111 1110						*/
	{illegal2,		2, 8, 8,L0|L1}, /* 7f: 0100 1000 0111 1111						*/

	{illegal2,		2, 8, 8,L0|L1}, /* 80: 0100 1000 1000 0000						*/
	{illegal2,		2, 8, 8,L0|L1}, /* 81: 0100 1000 1000 0001						*/
	{LDEAX_D,		2,14,11,L0|L1}, /* 82: 0100 1000 1000 0010						*/
	{LDEAX_H,		2,14,11,L0|L1}, /* 83: 0100 1000 1000 0011						*/
	{LDEAX_Dp,		2,14,11,L0|L1}, /* 84: 0100 1000 1000 0100						*/
	{LDEAX_Hp,		2,14,11,L0|L1}, /* 85: 0100 1000 1000 0101						*/
	{illegal2,		2, 8, 8,L0|L1}, /* 86: 0100 1000 1000 0110						*/
	{illegal2,		2, 8, 8,L0|L1}, /* 87: 0100 1000 1000 0111						*/
	{illegal2,		2, 8, 8,L0|L1}, /* 88: 0100 1000 1000 1000						*/
	{illegal2,		2, 8, 8,L0|L1}, /* 89: 0100 1000 1000 1001						*/
	{illegal2,		2, 8, 8,L0|L1}, /* 8a: 0100 1000 1000 1010						*/
	{LDEAX_D_xx,	3,20,20,L0|L1}, /* 8b: 0100 1000 1000 1011 xxxx xxxx			*/
	{LDEAX_H_A, 	2,20,20,L0|L1}, /* 8c: 0100 1000 1000 1100						*/
	{LDEAX_H_B, 	2,20,20,L0|L1}, /* 8d: 0100 1000 1000 1101						*/
	{LDEAX_H_EA,	2,20,20,L0|L1}, /* 8e: 0100 1000 1000 1110						*/
	{LDEAX_H_xx,	3,20,20,L0|L1}, /* 8f: 0100 1000 1000 1111 xxxx xxxx			*/

	{illegal2,		2, 8, 8,L0|L1}, /* 90: 0100 1000 1000 0000						*/
	{illegal2,		2, 8, 8,L0|L1}, /* 91: 0100 1000 1000 0001						*/
	{STEAX_D,		2,14,11,L0|L1}, /* 92: 0100 1000 1000 0010						*/
	{STEAX_H,		2,14,11,L0|L1}, /* 93: 0100 1000 1000 0011						*/
	{STEAX_Dp,		2,14,11,L0|L1}, /* 94: 0100 1000 1000 0100						*/
	{STEAX_Hp,		2,14,11,L0|L1}, /* 95: 0100 1000 1000 0101						*/
	{illegal2,		2, 8, 8,L0|L1}, /* 96: 0100 1000 1000 0110						*/
	{illegal2,		2, 8, 8,L0|L1}, /* 97: 0100 1000 1000 0111						*/
	{illegal2,		2, 8, 8,L0|L1}, /* 98: 0100 1000 1000 1000						*/
	{illegal2,		2, 8, 8,L0|L1}, /* 99: 0100 1000 1000 1001						*/
	{illegal2,		2, 8, 8,L0|L1}, /* 9a: 0100 1000 1000 1010						*/
	{STEAX_D_xx,	3,20,20,L0|L1}, /* 9b: 0100 1000 1000 1011 xxxx xxxx			*/
	{STEAX_H_A, 	2,20,20,L0|L1}, /* 9c: 0100 1000 1000 1100						*/
	{STEAX_H_B, 	2,20,20,L0|L1}, /* 9d: 0100 1000 1000 1101						*/
	{STEAX_H_EA,	2,20,20,L0|L1}, /* 9e: 0100 1000 1000 1110						*/
	{STEAX_H_xx,	3,20,20,L0|L1}, /* 9f: 0100 1000 1000 1111 xxxx xxxx			*/

	{DSLR_EA,		2, 8, 8,L0|L1}, /* a0: 0100 1000 1010 0000						*/
	{illegal2,		2, 8, 8,L0|L1}, /* a1: 0100 1000 1010 0001						*/
	{illegal2,		2, 8, 8,L0|L1}, /* a2: 0100 1000 1010 0010						*/
	{illegal2,		2, 8, 8,L0|L1}, /* a3: 0100 1000 1010 0011						*/
	{DSLL_EA,		2, 8, 8,L0|L1}, /* a4: 0100 1000 1010 0100						*/
	{illegal2,		2, 8, 8,L0|L1}, /* a5: 0100 1000 1010 0101						*/
	{illegal2,		2, 8, 8,L0|L1}, /* a6: 0100 1000 1010 0110						*/
	{illegal2,		2, 8, 8,L0|L1}, /* a7: 0100 1000 1010 0111						*/
	{TABLE, 		2,17,17,L0|L1}, /* a8: 0100 1000 1010 1000						*/
	{illegal2,		2, 8, 8,L0|L1}, /* a9: 0100 1000 1010 1001						*/
	{illegal2,		2, 8, 8,L0|L1}, /* aa: 0100 1000 1010 1010						*/
	{illegal2,		2, 8, 8,L0|L1}, /* ab: 0100 1000 1010 1011						*/
//	{illegal2,		2, 8, 8,L0|L1}, /* ac: 0100 1000 1010 1100						*/
//	{illegal2,		2, 8, 8,L0|L1}, /* ad: 0100 1000 1010 1101						*/
//	{illegal2,		2, 8, 8,L0|L1}, /* ae: 0100 1000 1010 1110						*/
//	{illegal2,		2, 8, 8,L0|L1}, /* af: 0100 1000 1010 1111						*/
	{EXA,			2, 8, 8,L0|L1}, /* ac: 0100 1000 1010 1100						*/	/* 7807 */
	{EXR,			2, 8, 8,L0|L1}, /* ad: 0100 1000 1010 1101						*/	/* 7807 */
	{EXH,			2, 8, 8,L0|L1}, /* ae: 0100 1000 1010 1110						*/	/* 7807 */
	{EXX,			2, 8, 8,L0|L1}, /* af: 0100 1000 1010 1111						*/	/* 7807 */
	{DRLR_EA,		2, 8, 8,L0|L1}, /* b0: 0100 1000 1011 0000						*/
	{illegal2,		2, 8, 8,L0|L1}, /* b1: 0100 1000 1011 0001						*/
	{illegal2,		2, 8, 8,L0|L1}, /* b2: 0100 1000 1011 0010						*/
	{illegal2,		2, 8, 8,L0|L1}, /* b3: 0100 1000 1011 0011						*/
	{DRLL_EA,		2, 8, 8,L0|L1}, /* b4: 0100 1000 1011 0100						*/
	{illegal2,		2, 8, 8,L0|L1}, /* b5: 0100 1000 1011 0101						*/
	{illegal2,		2, 8, 8,L0|L1}, /* b6: 0100 1000 1011 0110						*/
	{illegal2,		2, 8, 8,L0|L1}, /* b7: 0100 1000 1011 0111						*/
	{illegal2,		2, 8, 8,L0|L1}, /* b8: 0100 1000 1011 1000						*/
	{illegal2,		2, 8, 8,L0|L1}, /* b9: 0100 1000 1011 1001						*/
	{illegal2,		2, 8, 8,L0|L1}, /* ba: 0100 1000 1011 1010						*/
	{STOP,			2,12,12,L0|L1}, /* bb: 0100 1000 1011 1011						*/
	{illegal2,		2, 8, 8,L0|L1}, /* bc: 0100 1000 1011 1100						*/
	{illegal2,		2, 8, 8,L0|L1}, /* bd: 0100 1000 1011 1101						*/
	{illegal2,		2, 8, 8,L0|L1}, /* be: 0100 1000 1011 1110						*/
	{illegal2,		2, 8, 8,L0|L1}, /* bf: 0100 1000 1011 1111						*/

	{DMOV_EA_ECNT,	2,14,11,L0|L1}, /* c0: 0100 1000 1100 0000						*/
	{DMOV_EA_ECPT,	2,14,11,L0|L1}, /* c1: 0100 1000 1100 0001						*/
	{illegal2,		2, 8, 8,L0|L1}, /* c2: 0100 1000 1100 0010						*/
	{illegal2,		2, 8, 8,L0|L1}, /* c3: 0100 1000 1100 0011						*/
	{illegal2,		2, 8, 8,L0|L1}, /* c4: 0100 1000 1100 0100						*/
	{illegal2,		2, 8, 8,L0|L1}, /* c5: 0100 1000 1100 0101						*/
	{illegal2,		2, 8, 8,L0|L1}, /* c6: 0100 1000 1100 0110						*/
	{illegal2,		2, 8, 8,L0|L1}, /* c7: 0100 1000 1100 0111						*/
	{illegal2,		2, 8, 8,L0|L1}, /* c8: 0100 1000 1100 1000						*/
	{illegal2,		2, 8, 8,L0|L1}, /* c9: 0100 1000 1100 1001						*/
	{illegal2,		2, 8, 8,L0|L1}, /* ca: 0100 1000 1100 1010						*/
	{illegal2,		2, 8, 8,L0|L1}, /* cb: 0100 1000 1100 1011						*/
	{illegal2,		2, 8, 8,L0|L1}, /* cc: 0100 1000 1100 1100						*/
	{illegal2,		2, 8, 8,L0|L1}, /* cd: 0100 1000 1100 1101						*/
	{illegal2,		2, 8, 8,L0|L1}, /* ce: 0100 1000 1100 1110						*/
	{illegal2,		2, 8, 8,L0|L1}, /* cf: 0100 1000 1100 1111						*/

	{illegal2,		2, 8, 8,L0|L1}, /* d0: 0100 1000 1101 0000						*/
	{illegal2,		2, 8, 8,L0|L1}, /* d1: 0100 1000 1101 0001						*/
	{DMOV_ETM0_EA,	2,14,11,L0|L1}, /* d2: 0100 1000 1101 0010						*/
	{DMOV_ETM1_EA,	2,14,11,L0|L1}, /* d3: 0100 1000 1101 0011						*/
	{illegal2,		2, 8, 8,L0|L1}, /* d4: 0100 1000 1101 0100						*/
	{illegal2,		2, 8, 8,L0|L1}, /* d5: 0100 1000 1101 0101						*/
	{illegal2,		2, 8, 8,L0|L1}, /* d6: 0100 1000 1101 0110						*/
	{illegal2,		2, 8, 8,L0|L1}, /* d7: 0100 1000 1101 0111						*/
	{illegal2,		2, 8, 8,L0|L1}, /* d8: 0100 1000 1101 1000						*/
	{illegal2,		2, 8, 8,L0|L1}, /* d9: 0100 1000 1101 1001						*/
	{illegal2,		2, 8, 8,L0|L1}, /* da: 0100 1000 1101 1010						*/
	{illegal2,		2, 8, 8,L0|L1}, /* db: 0100 1000 1101 1011						*/
	{illegal2,		2, 8, 8,L0|L1}, /* dc: 0100 1000 1101 1100						*/
	{illegal2,		2, 8, 8,L0|L1}, /* dd: 0100 1000 1101 1101						*/
	{illegal2,		2, 8, 8,L0|L1}, /* de: 0100 1000 1101 1110						*/
	{illegal2,		2, 8, 8,L0|L1}, /* df: 0100 1000 1101 1111						*/

	{illegal2,		2, 8, 8,L0|L1}, /* e0: 0100 1000 1110 0000						*/
	{illegal2,		2, 8, 8,L0|L1}, /* e1: 0100 1000 1110 0001						*/
	{illegal2,		2, 8, 8,L0|L1}, /* e2: 0100 1000 1110 0010						*/
	{illegal2,		2, 8, 8,L0|L1}, /* e3: 0100 1000 1110 0011						*/
	{illegal2,		2, 8, 8,L0|L1}, /* e4: 0100 1000 1110 0100						*/
	{illegal2,		2, 8, 8,L0|L1}, /* e5: 0100 1000 1110 0101						*/
	{illegal2,		2, 8, 8,L0|L1}, /* e6: 0100 1000 1110 0110						*/
	{illegal2,		2, 8, 8,L0|L1}, /* e7: 0100 1000 1110 0111						*/
	{illegal2,		2, 8, 8,L0|L1}, /* e8: 0100 1000 1110 1000						*/
	{illegal2,		2, 8, 8,L0|L1}, /* e9: 0100 1000 1110 1001						*/
	{illegal2,		2, 8, 8,L0|L1}, /* ea: 0100 1000 1110 1010						*/
	{illegal2,		2, 8, 8,L0|L1}, /* eb: 0100 1000 1110 1011						*/
	{illegal2,		2, 8, 8,L0|L1}, /* ec: 0100 1000 1110 1100						*/
	{illegal2,		2, 8, 8,L0|L1}, /* ed: 0100 1000 1110 1101						*/
	{illegal2,		2, 8, 8,L0|L1}, /* ee: 0100 1000 1110 1110						*/
	{illegal2,		2, 8, 8,L0|L1}, /* ef: 0100 1000 1110 1111						*/

	{illegal2,		2, 8, 8,L0|L1}, /* f0: 0100 1000 1111 0000						*/
	{illegal2,		2, 8, 8,L0|L1}, /* f1: 0100 1000 1111 0001						*/
	{illegal2,		2, 8, 8,L0|L1}, /* f2: 0100 1000 1111 0010						*/
	{illegal2,		2, 8, 8,L0|L1}, /* f3: 0100 1000 1111 0011						*/
	{illegal2,		2, 8, 8,L0|L1}, /* f4: 0100 1000 1111 0100						*/
	{illegal2,		2, 8, 8,L0|L1}, /* f5: 0100 1000 1111 0101						*/
	{illegal2,		2, 8, 8,L0|L1}, /* f6: 0100 1000 1111 0110						*/
	{illegal2,		2, 8, 8,L0|L1}, /* f7: 0100 1000 1111 0111						*/
	{illegal2,		2, 8, 8,L0|L1}, /* f8: 0100 1000 1111 1000						*/
	{illegal2,		2, 8, 8,L0|L1}, /* f9: 0100 1000 1111 1001						*/
	{illegal2,		2, 8, 8,L0|L1}, /* fa: 0100 1000 1111 1010						*/
	{illegal2,		2, 8, 8,L0|L1}, /* fb: 0100 1000 1111 1011						*/
	{illegal2,		2, 8, 8,L0|L1}, /* fc: 0100 1000 1111 1100						*/
	{illegal2,		2, 8, 8,L0|L1}, /* fd: 0100 1000 1111 1101						*/
	{illegal2,		2, 8, 8,L0|L1}, /* fe: 0100 1000 1111 1110						*/
	{illegal2,		2, 8, 8,L0|L1}	/* ff: 0100 1000 1111 1111						*/
};

/* prefix 4C */
static struct opcode_s op4C[256] =
{
	{illegal2,		2, 8, 8,L0|L1}, /* 00: 0100 1100 0000 0000						*/
	{illegal2,		2, 8, 8,L0|L1}, /* 01: 0100 1100 0000 0001						*/
	{illegal2,		2, 8, 8,L0|L1}, /* 02: 0100 1100 0000 0010						*/
	{illegal2,		2, 8, 8,L0|L1}, /* 03: 0100 1100 0000 0011						*/
	{illegal2,		2, 8, 8,L0|L1}, /* 04: 0100 1100 0000 0100						*/
	{illegal2,		2, 8, 8,L0|L1}, /* 05: 0100 1100 0000 0101						*/
	{illegal2,		2, 8, 8,L0|L1}, /* 06: 0100 1100 0000 0110						*/
	{illegal2,		2, 8, 8,L0|L1}, /* 07: 0100 1100 0000 0111						*/
	{illegal2,		2, 8, 8,L0|L1}, /* 08: 0100 1100 0000 1000						*/
	{illegal2,		2, 8, 8,L0|L1}, /* 09: 0100 1100 0000 1001						*/
	{illegal2,		2, 8, 8,L0|L1}, /* 0a: 0100 1100 0000 1010						*/
	{illegal2,		2, 8, 8,L0|L1}, /* 0b: 0100 1100 0000 1011						*/
	{illegal2,		2, 8, 8,L0|L1}, /* 0c: 0100 1100 0000 1100						*/
	{illegal2,		2, 8, 8,L0|L1}, /* 0d: 0100 1100 0000 1101						*/
	{illegal2,		2, 8, 8,L0|L1}, /* 0e: 0100 1100 0000 1110						*/
	{illegal2,		2, 8, 8,L0|L1}, /* 0f: 0100 1100 0000 1111						*/

	{illegal2,		2, 8, 8,L0|L1}, /* 10: 0100 1100 0001 0000						*/
	{illegal2,		2, 8, 8,L0|L1}, /* 11: 0100 1100 0001 0001						*/
	{illegal2,		2, 8, 8,L0|L1}, /* 12: 0100 1100 0001 0010						*/
	{illegal2,		2, 8, 8,L0|L1}, /* 13: 0100 1100 0001 0011						*/
	{illegal2,		2, 8, 8,L0|L1}, /* 14: 0100 1100 0001 0100						*/
	{illegal2,		2, 8, 8,L0|L1}, /* 15: 0100 1100 0001 0101						*/
	{illegal2,		2, 8, 8,L0|L1}, /* 16: 0100 1100 0001 0110						*/
	{illegal2,		2, 8, 8,L0|L1}, /* 17: 0100 1100 0001 0111						*/
	{illegal2,		2, 8, 8,L0|L1}, /* 18: 0100 1100 0001 1000						*/
	{illegal2,		2, 8, 8,L0|L1}, /* 19: 0100 1100 0001 1001						*/
	{illegal2,		2, 8, 8,L0|L1}, /* 1a: 0100 1100 0001 1010						*/
	{illegal2,		2, 8, 8,L0|L1}, /* 1b: 0100 1100 0001 1011						*/
	{illegal2,		2, 8, 8,L0|L1}, /* 1c: 0100 1100 0001 1100						*/
	{illegal2,		2, 8, 8,L0|L1}, /* 1d: 0100 1100 0001 1101						*/
	{illegal2,		2, 8, 8,L0|L1}, /* 1e: 0100 1100 0001 1110						*/
	{illegal2,		2, 8, 8,L0|L1}, /* 1f: 0100 1100 0001 1111						*/

	{illegal2,		2, 8, 8,L0|L1}, /* 20: 0100 1100 0010 0000						*/
	{illegal2,		2, 8, 8,L0|L1}, /* 21: 0100 1100 0010 0001						*/
	{illegal2,		2, 8, 8,L0|L1}, /* 22: 0100 1100 0010 0010						*/
	{illegal2,		2, 8, 8,L0|L1}, /* 23: 0100 1100 0010 0011						*/
	{illegal2,		2, 8, 8,L0|L1}, /* 24: 0100 1100 0010 0100						*/
	{illegal2,		2, 8, 8,L0|L1}, /* 25: 0100 1100 0010 0101						*/
	{illegal2,		2, 8, 8,L0|L1}, /* 26: 0100 1100 0010 0110						*/
	{illegal2,		2, 8, 8,L0|L1}, /* 27: 0100 1100 0010 0111						*/
	{illegal2,		2, 8, 8,L0|L1}, /* 28: 0100 1100 0010 1000						*/
	{illegal2,		2, 8, 8,L0|L1}, /* 29: 0100 1100 0010 1001						*/
	{illegal2,		2, 8, 8,L0|L1}, /* 2a: 0100 1100 0010 1010						*/
	{illegal2,		2, 8, 8,L0|L1}, /* 2b: 0100 1100 0010 1011						*/
	{illegal2,		2, 8, 8,L0|L1}, /* 2c: 0100 1100 0010 1100						*/
	{illegal2,		2, 8, 8,L0|L1}, /* 2d: 0100 1100 0010 1101						*/
	{illegal2,		2, 8, 8,L0|L1}, /* 2e: 0100 1100 0010 1110						*/
	{illegal2,		2, 8, 8,L0|L1}, /* 2f: 0100 1100 0010 1111						*/

	{illegal2,		2, 8, 8,L0|L1}, /* 30: 0100 1100 0011 0000						*/
	{illegal2,		2, 8, 8,L0|L1}, /* 31: 0100 1100 0011 0001						*/
	{illegal2,		2, 8, 8,L0|L1}, /* 32: 0100 1100 0011 0010						*/
	{illegal2,		2, 8, 8,L0|L1}, /* 33: 0100 1100 0011 0011						*/
	{illegal2,		2, 8, 8,L0|L1}, /* 34: 0100 1100 0011 0100						*/
	{illegal2,		2, 8, 8,L0|L1}, /* 35: 0100 1100 0011 0101						*/
	{illegal2,		2, 8, 8,L0|L1}, /* 36: 0100 1100 0011 0110						*/
	{illegal2,		2, 8, 8,L0|L1}, /* 37: 0100 1100 0011 0111						*/
	{illegal2,		2, 8, 8,L0|L1}, /* 38: 0100 1100 0011 1000						*/
	{illegal2,		2, 8, 8,L0|L1}, /* 39: 0100 1100 0011 1001						*/
	{illegal2,		2, 8, 8,L0|L1}, /* 3a: 0100 1100 0011 1010						*/
	{illegal2,		2, 8, 8,L0|L1}, /* 3b: 0100 1100 0011 1011						*/
	{illegal2,		2, 8, 8,L0|L1}, /* 3c: 0100 1100 0011 1100						*/
	{illegal2,		2, 8, 8,L0|L1}, /* 3d: 0100 1100 0011 1101						*/
	{illegal2,		2, 8, 8,L0|L1}, /* 3e: 0100 1100 0011 1110						*/
	{illegal2,		2, 8, 8,L0|L1}, /* 3f: 0100 1100 0011 1111						*/

	{illegal2,		2, 8, 8,L0|L1}, /* 40: 0100 1100 0100 0000						*/
	{illegal2,		2, 8, 8,L0|L1}, /* 41: 0100 1100 0100 0001						*/
	{illegal2,		2, 8, 8,L0|L1}, /* 42: 0100 1100 0100 0010						*/
	{illegal2,		2, 8, 8,L0|L1}, /* 43: 0100 1100 0100 0011						*/
	{illegal2,		2, 8, 8,L0|L1}, /* 44: 0100 1100 0100 0100						*/
	{illegal2,		2, 8, 8,L0|L1}, /* 45: 0100 1100 0100 0101						*/
	{illegal2,		2, 8, 8,L0|L1}, /* 46: 0100 1100 0100 0110						*/
	{illegal2,		2, 8, 8,L0|L1}, /* 47: 0100 1100 0100 0111						*/
	{illegal2,		2, 8, 8,L0|L1}, /* 48: 0100 1100 0100 1000						*/
	{illegal2,		2, 8, 8,L0|L1}, /* 49: 0100 1100 0100 1001						*/
	{illegal2,		2, 8, 8,L0|L1}, /* 4a: 0100 1100 0100 1010						*/
	{illegal2,		2, 8, 8,L0|L1}, /* 4b: 0100 1100 0100 1011						*/
	{illegal2,		2, 8, 8,L0|L1}, /* 4c: 0100 1100 0100 1100						*/
	{illegal2,		2, 8, 8,L0|L1}, /* 4d: 0100 1100 0100 1101						*/
	{illegal2,		2, 8, 8,L0|L1}, /* 4e: 0100 1100 0100 1110						*/
	{illegal2,		2, 8, 8,L0|L1}, /* 4f: 0100 1100 0100 1111						*/

	{illegal2,		2, 8, 8,L0|L1}, /* 50: 0100 1100 0101 0000						*/
	{illegal2,		2, 8, 8,L0|L1}, /* 51: 0100 1100 0101 0001						*/
	{illegal2,		2, 8, 8,L0|L1}, /* 52: 0100 1100 0101 0010						*/
	{illegal2,		2, 8, 8,L0|L1}, /* 53: 0100 1100 0101 0011						*/
	{illegal2,		2, 8, 8,L0|L1}, /* 54: 0100 1100 0101 0100						*/
	{illegal2,		2, 8, 8,L0|L1}, /* 55: 0100 1100 0101 0101						*/
	{illegal2,		2, 8, 8,L0|L1}, /* 56: 0100 1100 0101 0110						*/
	{illegal2,		2, 8, 8,L0|L1}, /* 57: 0100 1100 0101 0111						*/
	{illegal2,		2, 8, 8,L0|L1}, /* 58: 0100 1100 0101 1000						*/
	{illegal2,		2, 8, 8,L0|L1}, /* 59: 0100 1100 0101 1001						*/
	{illegal2,		2, 8, 8,L0|L1}, /* 5a: 0100 1100 0101 1010						*/
	{illegal2,		2, 8, 8,L0|L1}, /* 5b: 0100 1100 0101 1011						*/
	{illegal2,		2, 8, 8,L0|L1}, /* 5c: 0100 1100 0101 1100						*/
	{illegal2,		2, 8, 8,L0|L1}, /* 5d: 0100 1100 0101 1101						*/
	{illegal2,		2, 8, 8,L0|L1}, /* 5e: 0100 1100 0101 1110						*/
	{illegal2,		2, 8, 8,L0|L1}, /* 5f: 0100 1100 0101 1111						*/

	{illegal2,		2, 8, 8,L0|L1}, /* 60: 0100 1100 0110 0000						*/
	{illegal2,		2, 8, 8,L0|L1}, /* 61: 0100 1100 0110 0001						*/
	{illegal2,		2, 8, 8,L0|L1}, /* 62: 0100 1100 0110 0010						*/
	{illegal2,		2, 8, 8,L0|L1}, /* 63: 0100 1100 0110 0011						*/
	{illegal2,		2, 8, 8,L0|L1}, /* 64: 0100 1100 0110 0100						*/
	{illegal2,		2, 8, 8,L0|L1}, /* 65: 0100 1100 0110 0101						*/
	{illegal2,		2, 8, 8,L0|L1}, /* 66: 0100 1100 0110 0110						*/
	{illegal2,		2, 8, 8,L0|L1}, /* 67: 0100 1100 0110 0111						*/
	{illegal2,		2, 8, 8,L0|L1}, /* 68: 0100 1100 0110 1000						*/
	{illegal2,		2, 8, 8,L0|L1}, /* 69: 0100 1100 0110 1001						*/
	{illegal2,		2, 8, 8,L0|L1}, /* 6a: 0100 1100 0110 1010						*/
	{illegal2,		2, 8, 8,L0|L1}, /* 6b: 0100 1100 0110 1011						*/
	{illegal2,		2, 8, 8,L0|L1}, /* 6c: 0100 1100 0110 1100						*/
	{illegal2,		2, 8, 8,L0|L1}, /* 6d: 0100 1100 0110 1101						*/
	{illegal2,		2, 8, 8,L0|L1}, /* 6e: 0100 1100 0110 1110						*/
	{illegal2,		2, 8, 8,L0|L1}, /* 6f: 0100 1100 0110 1111						*/

	{illegal2,		2, 8, 8,L0|L1}, /* 70: 0100 1100 0111 0000						*/
	{illegal2,		2, 8, 8,L0|L1}, /* 71: 0100 1100 0111 0001						*/
	{illegal2,		2, 8, 8,L0|L1}, /* 72: 0100 1100 0111 0010						*/
	{illegal2,		2, 8, 8,L0|L1}, /* 73: 0100 1100 0111 0011						*/
	{illegal2,		2, 8, 8,L0|L1}, /* 74: 0100 1100 0111 0100						*/
	{illegal2,		2, 8, 8,L0|L1}, /* 75: 0100 1100 0111 0101						*/
	{illegal2,		2, 8, 8,L0|L1}, /* 76: 0100 1100 0111 0110						*/
	{illegal2,		2, 8, 8,L0|L1}, /* 77: 0100 1100 0111 0111						*/
	{illegal2,		2, 8, 8,L0|L1}, /* 78: 0100 1100 0111 1000						*/
	{illegal2,		2, 8, 8,L0|L1}, /* 79: 0100 1100 0111 1001						*/
	{illegal2,		2, 8, 8,L0|L1}, /* 7a: 0100 1100 0111 1010						*/
	{illegal2,		2, 8, 8,L0|L1}, /* 7b: 0100 1100 0111 1011						*/
	{illegal2,		2, 8, 8,L0|L1}, /* 7c: 0100 1100 0111 1100						*/
	{illegal2,		2, 8, 8,L0|L1}, /* 7d: 0100 1100 0111 1101						*/
	{illegal2,		2, 8, 8,L0|L1}, /* 7e: 0100 1100 0111 1110						*/
	{illegal2,		2, 8, 8,L0|L1}, /* 7f: 0100 1100 0111 1111						*/

	{illegal2,		2, 8, 8,L0|L1}, /* 80: 0100 1100 1000 0000						*/
	{illegal2,		2, 8, 8,L0|L1}, /* 81: 0100 1100 1000 0001						*/
	{illegal2,		2, 8, 8,L0|L1}, /* 82: 0100 1100 1000 0010						*/
	{illegal2,		2, 8, 8,L0|L1}, /* 83: 0100 1100 1000 0011						*/
	{illegal2,		2, 8, 8,L0|L1}, /* 84: 0100 1100 1000 0100						*/
	{illegal2,		2, 8, 8,L0|L1}, /* 85: 0100 1100 1000 0101						*/
	{illegal2,		2, 8, 8,L0|L1}, /* 86: 0100 1100 1000 0110						*/
	{illegal2,		2, 8, 8,L0|L1}, /* 87: 0100 1100 1000 0111						*/
	{illegal2,		2, 8, 8,L0|L1}, /* 88: 0100 1100 1000 1000						*/
	{illegal2,		2, 8, 8,L0|L1}, /* 89: 0100 1100 1000 1001						*/
	{illegal2,		2, 8, 8,L0|L1}, /* 8a: 0100 1100 1000 1010						*/
	{illegal2,		2, 8, 8,L0|L1}, /* 8b: 0100 1100 1000 1011						*/
	{illegal2,		2, 8, 8,L0|L1}, /* 8c: 0100 1100 1000 1100						*/
	{illegal2,		2, 8, 8,L0|L1}, /* 8d: 0100 1100 1000 1101						*/
	{illegal2,		2, 8, 8,L0|L1}, /* 8e: 0100 1100 1000 1110						*/
	{illegal2,		2, 8, 8,L0|L1}, /* 8f: 0100 1100 1000 1111						*/

	{illegal2,		2, 8, 8,L0|L1}, /* 90: 0100 1100 1001 0000						*/
	{illegal2,		2, 8, 8,L0|L1}, /* 91: 0100 1100 1001 0001						*/
	{illegal2,		2, 8, 8,L0|L1}, /* 92: 0100 1100 1001 0010						*/
	{illegal2,		2, 8, 8,L0|L1}, /* 93: 0100 1100 1001 0011						*/
	{illegal2,		2, 8, 8,L0|L1}, /* 94: 0100 1100 1001 0100						*/
	{illegal2,		2, 8, 8,L0|L1}, /* 95: 0100 1100 1001 0101						*/
	{illegal2,		2, 8, 8,L0|L1}, /* 96: 0100 1100 1001 0110						*/
	{illegal2,		2, 8, 8,L0|L1}, /* 97: 0100 1100 1001 0111						*/
	{illegal2,		2, 8, 8,L0|L1}, /* 98: 0100 1100 1001 1000						*/
	{illegal2,		2, 8, 8,L0|L1}, /* 99: 0100 1100 1001 1001						*/
	{illegal2,		2, 8, 8,L0|L1}, /* 9a: 0100 1100 1001 1010						*/
	{illegal2,		2, 8, 8,L0|L1}, /* 9b: 0100 1100 1001 1011						*/
	{illegal2,		2, 8, 8,L0|L1}, /* 9c: 0100 1100 1001 1100						*/
	{illegal2,		2, 8, 8,L0|L1}, /* 9d: 0100 1100 1001 1101						*/
	{illegal2,		2, 8, 8,L0|L1}, /* 9e: 0100 1100 1001 1110						*/
	{illegal2,		2, 8, 8,L0|L1}, /* 9f: 0100 1100 1001 1111						*/

	{illegal2,		2, 8, 8,L0|L1}, /* a0: 0100 1100 1010 0000						*/
	{illegal2,		2, 8, 8,L0|L1}, /* a1: 0100 1100 1010 0001						*/
	{illegal2,		2, 8, 8,L0|L1}, /* a2: 0100 1100 1010 0010						*/
	{illegal2,		2, 8, 8,L0|L1}, /* a3: 0100 1100 1010 0011						*/
	{illegal2,		2, 8, 8,L0|L1}, /* a4: 0100 1100 1010 0100						*/
	{illegal2,		2, 8, 8,L0|L1}, /* a5: 0100 1100 1010 0101						*/
	{illegal2,		2, 8, 8,L0|L1}, /* a6: 0100 1100 1010 0110						*/
	{illegal2,		2, 8, 8,L0|L1}, /* a7: 0100 1100 1010 0111						*/
	{illegal2,		2, 8, 8,L0|L1}, /* a8: 0100 1100 1010 1000						*/
	{illegal2,		2, 8, 8,L0|L1}, /* a9: 0100 1100 1010 1001						*/
	{illegal2,		2, 8, 8,L0|L1}, /* aa: 0100 1100 1010 1010						*/
	{illegal2,		2, 8, 8,L0|L1}, /* ab: 0100 1100 1010 1011						*/
	{illegal2,		2, 8, 8,L0|L1}, /* ac: 0100 1100 1010 1100						*/
	{illegal2,		2, 8, 8,L0|L1}, /* ad: 0100 1100 1010 1101						*/
	{illegal2,		2, 8, 8,L0|L1}, /* ae: 0100 1100 1010 1110						*/
	{illegal2,		2, 8, 8,L0|L1}, /* af: 0100 1100 1010 1111						*/

	{illegal2,		2, 8, 8,L0|L1}, /* b0: 0100 1100 1011 0000						*/
	{illegal2,		2, 8, 8,L0|L1}, /* b1: 0100 1100 1011 0001						*/
	{illegal2,		2, 8, 8,L0|L1}, /* b2: 0100 1100 1011 0010						*/
	{illegal2,		2, 8, 8,L0|L1}, /* b3: 0100 1100 1011 0011						*/
	{illegal2,		2, 8, 8,L0|L1}, /* b4: 0100 1100 1011 0100						*/
	{illegal2,		2, 8, 8,L0|L1}, /* b5: 0100 1100 1011 0101						*/
	{illegal2,		2, 8, 8,L0|L1}, /* b6: 0100 1100 1011 0110						*/
	{illegal2,		2, 8, 8,L0|L1}, /* b7: 0100 1100 1011 0111						*/
	{illegal2,		2, 8, 8,L0|L1}, /* b8: 0100 1100 1011 1000						*/
	{illegal2,		2, 8, 8,L0|L1}, /* b9: 0100 1100 1011 1001						*/
	{illegal2,		2, 8, 8,L0|L1}, /* ba: 0100 1100 1011 1010						*/
	{illegal2,		2, 8, 8,L0|L1}, /* bb: 0100 1100 1011 1011						*/
	{illegal2,		2, 8, 8,L0|L1}, /* bc: 0100 1100 1011 1100						*/
	{illegal2,		2, 8, 8,L0|L1}, /* bd: 0100 1100 1011 1101						*/
	{illegal2,		2, 8, 8,L0|L1}, /* be: 0100 1100 1011 1110						*/
	{illegal2,		2, 8, 8,L0|L1}, /* bf: 0100 1100 1011 1111						*/

	{MOV_A_PA,		2,10,10,L0|L1}, /* c0: 0100 1100 1100 0000						*/
	{MOV_A_PB,		2,10,10,L0|L1}, /* c1: 0100 1100 1100 0001						*/
	{MOV_A_PC,		2,10,10,L0|L1}, /* c2: 0100 1100 1100 0010						*/
	{MOV_A_PD,		2,10,10,L0|L1}, /* c3: 0100 1100 1100 0011						*/
	{illegal2,		2,10,10,L0|L1}, /* c4: 0100 1100 1100 0100						*/
	{MOV_A_PF,		2,10,10,L0|L1}, /* c5: 0100 1100 1100 0101						*/
	{MOV_A_MKH, 	2,10,10,L0|L1}, /* c6: 0100 1100 1100 0110						*/
	{MOV_A_MKL, 	2,10,10,L0|L1}, /* c7: 0100 1100 1100 0111						*/
	{MOV_A_ANM, 	2,10,10,L0|L1}, /* c8: 0100 1100 1100 1000						*/
	{MOV_A_SMH, 	2,10,10,L0|L1}, /* c9: 0100 1100 1100 1001						*/
	{illegal2,		2,10,10,L0|L1}, /* ca: 0100 1100 1100 1010						*/
	{MOV_A_EOM, 	2,10,10,L0|L1}, /* cb: 0100 1100 1100 1011						*/
	{illegal2,		2,10,10,L0|L1}, /* cc: 0100 1100 1100 1100						*/
	{MOV_A_TMM, 	2,10,10,L0|L1}, /* cd: 0100 1100 1100 1101						*/
//	{illegal2,		2,10,10,L0|L1}, /* ce: 0100 1100 1100 1110						*/
	{MOV_A_PT, 		2,10,10,L0|L1}, /* ce: 0100 1100 1100 1110						*/	/* 7807 */
	{illegal2,		2,10,10,L0|L1}, /* cf: 0100 1100 1100 1111						*/

	{illegal2,		2,10,10,L0|L1}, /* d0: 0100 1100 1101 0000						*/
	{illegal2,		2,10,10,L0|L1}, /* d1: 0100 1100 1101 0001						*/
	{illegal2,		2,10,10,L0|L1}, /* d2: 0100 1100 1101 0010						*/
	{illegal2,		2,10,10,L0|L1}, /* d3: 0100 1100 1101 0011						*/
	{illegal2,		2,10,10,L0|L1}, /* d4: 0100 1100 1101 0100						*/
	{illegal2,		2,10,10,L0|L1}, /* d5: 0100 1100 1101 0101						*/
	{illegal2,		2,10,10,L0|L1}, /* d6: 0100 1100 1101 0110						*/
	{illegal2,		2,10,10,L0|L1}, /* d7: 0100 1100 1101 0111						*/
	{illegal2,		2,10,10,L0|L1}, /* d8: 0100 1100 1101 1000						*/
	{MOV_A_RXB, 	2,10,10,L0|L1}, /* d9: 0100 1100 1101 1001						*/
	{illegal2,		2,10,10,L0|L1}, /* da: 0100 1100 1101 1010						*/
	{illegal2,		2,10,10,L0|L1}, /* db: 0100 1100 1101 1011						*/
	{illegal2,		2,10,10,L0|L1}, /* dc: 0100 1100 1101 1100						*/
	{illegal2,		2,10,10,L0|L1}, /* dd: 0100 1100 1101 1101						*/
	{illegal2,		2,10,10,L0|L1}, /* de: 0100 1100 1101 1110						*/
	{illegal2,		2,10,10,L0|L1}, /* df: 0100 1100 1101 1111						*/

	{MOV_A_CR0, 	2,10,10,L0|L1}, /* e0: 0100 1100 1110 0000						*/
	{MOV_A_CR1, 	2,10,10,L0|L1}, /* e1: 0100 1100 1110 0001						*/
	{MOV_A_CR2, 	2,10,10,L0|L1}, /* e2: 0100 1100 1110 0010						*/
	{MOV_A_CR3, 	2,10,10,L0|L1}, /* e3: 0100 1100 1110 0011						*/
	{illegal2,		2,10,10,L0|L1}, /* e4: 0100 1100 1110 0100						*/
	{illegal2,		2,10,10,L0|L1}, /* e5: 0100 1100 1110 0101						*/
	{illegal2,		2,10,10,L0|L1}, /* e6: 0100 1100 1110 0110						*/
	{illegal2,		2,10,10,L0|L1}, /* e7: 0100 1100 1110 0111						*/
	{illegal2,		2,10,10,L0|L1}, /* e8: 0100 1100 1110 1000						*/
	{illegal2,		2,10,10,L0|L1}, /* e9: 0100 1100 1110 1001						*/
	{illegal2,		2,10,10,L0|L1}, /* ea: 0100 1100 1110 1010						*/
	{illegal2,		2,10,10,L0|L1}, /* eb: 0100 1100 1110 1011						*/
	{illegal2,		2,10,10,L0|L1}, /* ec: 0100 1100 1110 1100						*/
	{illegal2,		2,10,10,L0|L1}, /* ed: 0100 1100 1110 1101						*/
	{illegal2,		2,10,10,L0|L1}, /* ee: 0100 1100 1110 1110						*/
	{illegal2,		2,10,10,L0|L1}, /* ef: 0100 1100 1110 1111						*/

	{illegal2,		2,10,10,L0|L1}, /* f0: 0100 1100 1111 0000						*/
	{illegal2,		2,10,10,L0|L1}, /* f1: 0100 1100 1111 0001						*/
	{illegal2,		2,10,10,L0|L1}, /* f2: 0100 1100 1111 0010						*/
	{illegal2,		2,10,10,L0|L1}, /* f3: 0100 1100 1111 0011						*/
	{illegal2,		2,10,10,L0|L1}, /* f4: 0100 1100 1111 0100						*/
	{illegal2,		2,10,10,L0|L1}, /* f5: 0100 1100 1111 0101						*/
	{illegal2,		2,10,10,L0|L1}, /* f6: 0100 1100 1111 0110						*/
	{illegal2,		2,10,10,L0|L1}, /* f7: 0100 1100 1111 0111						*/
	{illegal2,		2,10,10,L0|L1}, /* f8: 0100 1100 1111 1000						*/
	{illegal2,		2,10,10,L0|L1}, /* f9: 0100 1100 1111 1001						*/
	{illegal2,		2,10,10,L0|L1}, /* fa: 0100 1100 1111 1010						*/
	{illegal2,		2,10,10,L0|L1}, /* fb: 0100 1100 1111 1011						*/
	{illegal2,		2,10,10,L0|L1}, /* fc: 0100 1100 1111 1100						*/
	{illegal2,		2,10,10,L0|L1}, /* fd: 0100 1100 1111 1101						*/
	{illegal2,		2,10,10,L0|L1}, /* fe: 0100 1100 1111 1110						*/
	{illegal2,		2,10,10,L0|L1}, /* ff: 0100 1100 1111 1111						*/
};

/* prefix 4D */
static struct opcode_s op4D[256] =
{
	{illegal2,		2, 8, 8,L0|L1}, /* 00: 0100 1101 0000 0000						*/
	{illegal2,		2, 8, 8,L0|L1}, /* 01: 0100 1101 0000 0001						*/
	{illegal2,		2, 8, 8,L0|L1}, /* 02: 0100 1101 0000 0010						*/
	{illegal2,		2, 8, 8,L0|L1}, /* 03: 0100 1101 0000 0011						*/
	{illegal2,		2, 8, 8,L0|L1}, /* 04: 0100 1101 0000 0100						*/
	{illegal2,		2, 8, 8,L0|L1}, /* 05: 0100 1101 0000 0101						*/
	{illegal2,		2, 8, 8,L0|L1}, /* 06: 0100 1101 0000 0110						*/
	{illegal2,		2, 8, 8,L0|L1}, /* 07: 0100 1101 0000 0111						*/
	{illegal2,		2, 8, 8,L0|L1}, /* 08: 0100 1101 0000 1000						*/
	{illegal2,		2, 8, 8,L0|L1}, /* 09: 0100 1101 0000 1001						*/
	{illegal2,		2, 8, 8,L0|L1}, /* 0a: 0100 1101 0000 1010						*/
	{illegal2,		2, 8, 8,L0|L1}, /* 0b: 0100 1101 0000 1011						*/
	{illegal2,		2, 8, 8,L0|L1}, /* 0c: 0100 1101 0000 1100						*/
	{illegal2,		2, 8, 8,L0|L1}, /* 0d: 0100 1101 0000 1101						*/
	{illegal2,		2, 8, 8,L0|L1}, /* 0e: 0100 1101 0000 1110						*/
	{illegal2,		2, 8, 8,L0|L1}, /* 0f: 0100 1101 0000 1111						*/

	{illegal2,		2, 8, 8,L0|L1}, /* 10: 0100 1101 0001 0000						*/
	{illegal2,		2, 8, 8,L0|L1}, /* 11: 0100 1101 0001 0001						*/
	{illegal2,		2, 8, 8,L0|L1}, /* 12: 0100 1101 0001 0010						*/
	{illegal2,		2, 8, 8,L0|L1}, /* 13: 0100 1101 0001 0011						*/
	{illegal2,		2, 8, 8,L0|L1}, /* 14: 0100 1101 0001 0100						*/
	{illegal2,		2, 8, 8,L0|L1}, /* 15: 0100 1101 0001 0101						*/
	{illegal2,		2, 8, 8,L0|L1}, /* 16: 0100 1101 0001 0110						*/
	{illegal2,		2, 8, 8,L0|L1}, /* 17: 0100 1101 0001 0111						*/
	{illegal2,		2, 8, 8,L0|L1}, /* 18: 0100 1101 0001 1000						*/
	{illegal2,		2, 8, 8,L0|L1}, /* 19: 0100 1101 0001 1001						*/
	{illegal2,		2, 8, 8,L0|L1}, /* 1a: 0100 1101 0001 1010						*/
	{illegal2,		2, 8, 8,L0|L1}, /* 1b: 0100 1101 0001 1011						*/
	{illegal2,		2, 8, 8,L0|L1}, /* 1c: 0100 1101 0001 1100						*/
	{illegal2,		2, 8, 8,L0|L1}, /* 1d: 0100 1101 0001 1101						*/
	{illegal2,		2, 8, 8,L0|L1}, /* 1e: 0100 1101 0001 1110						*/
	{illegal2,		2, 8, 8,L0|L1}, /* 1f: 0100 1101 0001 1111						*/

	{illegal2,		2, 8, 8,L0|L1}, /* 20: 0100 1101 0010 0000						*/
	{illegal2,		2, 8, 8,L0|L1}, /* 21: 0100 1101 0010 0001						*/
	{illegal2,		2, 8, 8,L0|L1}, /* 22: 0100 1101 0010 0010						*/
	{illegal2,		2, 8, 8,L0|L1}, /* 23: 0100 1101 0010 0011						*/
	{illegal2,		2, 8, 8,L0|L1}, /* 24: 0100 1101 0010 0100						*/
	{illegal2,		2, 8, 8,L0|L1}, /* 25: 0100 1101 0010 0101						*/
	{illegal2,		2, 8, 8,L0|L1}, /* 26: 0100 1101 0010 0110						*/
	{illegal2,		2, 8, 8,L0|L1}, /* 27: 0100 1101 0010 0111						*/
	{illegal2,		2, 8, 8,L0|L1}, /* 28: 0100 1101 0010 1000						*/
	{illegal2,		2, 8, 8,L0|L1}, /* 29: 0100 1101 0010 1001						*/
	{illegal2,		2, 8, 8,L0|L1}, /* 2a: 0100 1101 0010 1010						*/
	{illegal2,		2, 8, 8,L0|L1}, /* 2b: 0100 1101 0010 1011						*/
	{illegal2,		2, 8, 8,L0|L1}, /* 2c: 0100 1101 0010 1100						*/
	{illegal2,		2, 8, 8,L0|L1}, /* 2d: 0100 1101 0010 1101						*/
	{illegal2,		2, 8, 8,L0|L1}, /* 2e: 0100 1101 0010 1110						*/
	{illegal2,		2, 8, 8,L0|L1}, /* 2f: 0100 1101 0010 1111						*/

	{illegal2,		2, 8, 8,L0|L1}, /* 30: 0100 1101 0011 0000						*/
	{illegal2,		2, 8, 8,L0|L1}, /* 31: 0100 1101 0011 0001						*/
	{illegal2,		2, 8, 8,L0|L1}, /* 32: 0100 1101 0011 0010						*/
	{illegal2,		2, 8, 8,L0|L1}, /* 33: 0100 1101 0011 0011						*/
	{illegal2,		2, 8, 8,L0|L1}, /* 34: 0100 1101 0011 0100						*/
	{illegal2,		2, 8, 8,L0|L1}, /* 35: 0100 1101 0011 0101						*/
	{illegal2,		2, 8, 8,L0|L1}, /* 36: 0100 1101 0011 0110						*/
	{illegal2,		2, 8, 8,L0|L1}, /* 37: 0100 1101 0011 0111						*/
	{illegal2,		2, 8, 8,L0|L1}, /* 38: 0100 1101 0011 1000						*/
	{illegal2,		2, 8, 8,L0|L1}, /* 39: 0100 1101 0011 1001						*/
	{illegal2,		2, 8, 8,L0|L1}, /* 3a: 0100 1101 0011 1010						*/
	{illegal2,		2, 8, 8,L0|L1}, /* 3b: 0100 1101 0011 1011						*/
	{illegal2,		2, 8, 8,L0|L1}, /* 3c: 0100 1101 0011 1100						*/
	{illegal2,		2, 8, 8,L0|L1}, /* 3d: 0100 1101 0011 1101						*/
	{illegal2,		2, 8, 8,L0|L1}, /* 3e: 0100 1101 0011 1110						*/
	{illegal2,		2, 8, 8,L0|L1}, /* 3f: 0100 1101 0011 1111						*/

	{illegal2,		2, 8, 8,L0|L1}, /* 40: 0100 1101 0100 0000						*/
	{illegal2,		2, 8, 8,L0|L1}, /* 41: 0100 1101 0100 0001						*/
	{illegal2,		2, 8, 8,L0|L1}, /* 42: 0100 1101 0100 0010						*/
	{illegal2,		2, 8, 8,L0|L1}, /* 43: 0100 1101 0100 0011						*/
	{illegal2,		2, 8, 8,L0|L1}, /* 44: 0100 1101 0100 0100						*/
	{illegal2,		2, 8, 8,L0|L1}, /* 45: 0100 1101 0100 0101						*/
	{illegal2,		2, 8, 8,L0|L1}, /* 46: 0100 1101 0100 0110						*/
	{illegal2,		2, 8, 8,L0|L1}, /* 47: 0100 1101 0100 0111						*/
	{illegal2,		2, 8, 8,L0|L1}, /* 48: 0100 1101 0100 1000						*/
	{illegal2,		2, 8, 8,L0|L1}, /* 49: 0100 1101 0100 1001						*/
	{illegal2,		2, 8, 8,L0|L1}, /* 4a: 0100 1101 0100 1010						*/
	{illegal2,		2, 8, 8,L0|L1}, /* 4b: 0100 1101 0100 1011						*/
	{illegal2,		2, 8, 8,L0|L1}, /* 4c: 0100 1101 0100 1100						*/
	{illegal2,		2, 8, 8,L0|L1}, /* 4d: 0100 1101 0100 1101						*/
	{illegal2,		2, 8, 8,L0|L1}, /* 4e: 0100 1101 0100 1110						*/
	{illegal2,		2, 8, 8,L0|L1}, /* 4f: 0100 1101 0100 1111						*/

	{illegal2,		2, 8, 8,L0|L1}, /* 50: 0100 1101 0101 0000						*/
	{illegal2,		2, 8, 8,L0|L1}, /* 51: 0100 1101 0101 0001						*/
	{illegal2,		2, 8, 8,L0|L1}, /* 52: 0100 1101 0101 0010						*/
	{illegal2,		2, 8, 8,L0|L1}, /* 53: 0100 1101 0101 0011						*/
	{illegal2,		2, 8, 8,L0|L1}, /* 54: 0100 1101 0101 0100						*/
	{illegal2,		2, 8, 8,L0|L1}, /* 55: 0100 1101 0101 0101						*/
	{illegal2,		2, 8, 8,L0|L1}, /* 56: 0100 1101 0101 0110						*/
	{illegal2,		2, 8, 8,L0|L1}, /* 57: 0100 1101 0101 0111						*/
	{illegal2,		2, 8, 8,L0|L1}, /* 58: 0100 1101 0101 1000						*/
	{illegal2,		2, 8, 8,L0|L1}, /* 59: 0100 1101 0101 1001						*/
	{illegal2,		2, 8, 8,L0|L1}, /* 5a: 0100 1101 0101 1010						*/
	{illegal2,		2, 8, 8,L0|L1}, /* 5b: 0100 1101 0101 1011						*/
	{illegal2,		2, 8, 8,L0|L1}, /* 5c: 0100 1101 0101 1100						*/
	{illegal2,		2, 8, 8,L0|L1}, /* 5d: 0100 1101 0101 1101						*/
	{illegal2,		2, 8, 8,L0|L1}, /* 5e: 0100 1101 0101 1110						*/
	{illegal2,		2, 8, 8,L0|L1}, /* 5f: 0100 1101 0101 1111						*/

	{illegal2,		2, 8, 8,L0|L1}, /* 60: 0100 1101 0110 0000						*/
	{illegal2,		2, 8, 8,L0|L1}, /* 61: 0100 1101 0110 0001						*/
	{illegal2,		2, 8, 8,L0|L1}, /* 62: 0100 1101 0110 0010						*/
	{illegal2,		2, 8, 8,L0|L1}, /* 63: 0100 1101 0110 0011						*/
	{illegal2,		2, 8, 8,L0|L1}, /* 64: 0100 1101 0110 0100						*/
	{illegal2,		2, 8, 8,L0|L1}, /* 65: 0100 1101 0110 0101						*/
	{illegal2,		2, 8, 8,L0|L1}, /* 66: 0100 1101 0110 0110						*/
	{illegal2,		2, 8, 8,L0|L1}, /* 67: 0100 1101 0110 0111						*/
	{illegal2,		2, 8, 8,L0|L1}, /* 68: 0100 1101 0110 1000						*/
	{illegal2,		2, 8, 8,L0|L1}, /* 69: 0100 1101 0110 1001						*/
	{illegal2,		2, 8, 8,L0|L1}, /* 6a: 0100 1101 0110 1010						*/
	{illegal2,		2, 8, 8,L0|L1}, /* 6b: 0100 1101 0110 1011						*/
	{illegal2,		2, 8, 8,L0|L1}, /* 6c: 0100 1101 0110 1100						*/
	{illegal2,		2, 8, 8,L0|L1}, /* 6d: 0100 1101 0110 1101						*/
	{illegal2,		2, 8, 8,L0|L1}, /* 6e: 0100 1101 0110 1110						*/
	{illegal2,		2, 8, 8,L0|L1}, /* 6f: 0100 1101 0110 1111						*/

	{illegal2,		2, 8, 8,L0|L1}, /* 70: 0100 1101 0111 0000						*/
	{illegal2,		2, 8, 8,L0|L1}, /* 71: 0100 1101 0111 0001						*/
	{illegal2,		2, 8, 8,L0|L1}, /* 72: 0100 1101 0111 0010						*/
	{illegal2,		2, 8, 8,L0|L1}, /* 73: 0100 1101 0111 0011						*/
	{illegal2,		2, 8, 8,L0|L1}, /* 74: 0100 1101 0111 0100						*/
	{illegal2,		2, 8, 8,L0|L1}, /* 75: 0100 1101 0111 0101						*/
	{illegal2,		2, 8, 8,L0|L1}, /* 76: 0100 1101 0111 0110						*/
	{illegal2,		2, 8, 8,L0|L1}, /* 77: 0100 1101 0111 0111						*/
	{illegal2,		2, 8, 8,L0|L1}, /* 78: 0100 1101 0111 1000						*/
	{illegal2,		2, 8, 8,L0|L1}, /* 79: 0100 1101 0111 1001						*/
	{illegal2,		2, 8, 8,L0|L1}, /* 7a: 0100 1101 0111 1010						*/
	{illegal2,		2, 8, 8,L0|L1}, /* 7b: 0100 1101 0111 1011						*/
	{illegal2,		2, 8, 8,L0|L1}, /* 7c: 0100 1101 0111 1100						*/
	{illegal2,		2, 8, 8,L0|L1}, /* 7d: 0100 1101 0111 1101						*/
	{illegal2,		2, 8, 8,L0|L1}, /* 7e: 0100 1101 0111 1110						*/
	{illegal2,		2, 8, 8,L0|L1}, /* 7f: 0100 1101 0111 1111						*/

	{illegal2,		2, 8, 8,L0|L1}, /* 80: 0100 1101 1000 0000						*/
	{illegal2,		2, 8, 8,L0|L1}, /* 81: 0100 1101 1000 0001						*/
	{illegal2,		2, 8, 8,L0|L1}, /* 82: 0100 1101 1000 0010						*/
	{illegal2,		2, 8, 8,L0|L1}, /* 83: 0100 1101 1000 0011						*/
	{illegal2,		2, 8, 8,L0|L1}, /* 84: 0100 1101 1000 0100						*/
	{illegal2,		2, 8, 8,L0|L1}, /* 85: 0100 1101 1000 0101						*/
	{illegal2,		2, 8, 8,L0|L1}, /* 86: 0100 1101 1000 0110						*/
	{illegal2,		2, 8, 8,L0|L1}, /* 87: 0100 1101 1000 0111						*/
	{illegal2,		2, 8, 8,L0|L1}, /* 88: 0100 1101 1000 1000						*/
	{illegal2,		2, 8, 8,L0|L1}, /* 89: 0100 1101 1000 1001						*/
	{illegal2,		2, 8, 8,L0|L1}, /* 8a: 0100 1101 1000 1010						*/
	{illegal2,		2, 8, 8,L0|L1}, /* 8b: 0100 1101 1000 1011						*/
	{illegal2,		2, 8, 8,L0|L1}, /* 8c: 0100 1101 1000 1100						*/
	{illegal2,		2, 8, 8,L0|L1}, /* 8d: 0100 1101 1000 1101						*/
	{illegal2,		2, 8, 8,L0|L1}, /* 8e: 0100 1101 1000 1110						*/
	{illegal2,		2, 8, 8,L0|L1}, /* 8f: 0100 1101 1000 1111						*/

	{illegal2,		2, 8, 8,L0|L1}, /* 90: 0100 1101 1001 0000						*/
	{illegal2,		2, 8, 8,L0|L1}, /* 91: 0100 1101 1001 0001						*/
	{illegal2,		2, 8, 8,L0|L1}, /* 92: 0100 1101 1001 0010						*/
	{illegal2,		2, 8, 8,L0|L1}, /* 93: 0100 1101 1001 0011						*/
	{illegal2,		2, 8, 8,L0|L1}, /* 94: 0100 1101 1001 0100						*/
	{illegal2,		2, 8, 8,L0|L1}, /* 95: 0100 1101 1001 0101						*/
	{illegal2,		2, 8, 8,L0|L1}, /* 96: 0100 1101 1001 0110						*/
	{illegal2,		2, 8, 8,L0|L1}, /* 97: 0100 1101 1001 0111						*/
	{illegal2,		2, 8, 8,L0|L1}, /* 98: 0100 1101 1001 1000						*/
	{illegal2,		2, 8, 8,L0|L1}, /* 99: 0100 1101 1001 1001						*/
	{illegal2,		2, 8, 8,L0|L1}, /* 9a: 0100 1101 1001 1010						*/
	{illegal2,		2, 8, 8,L0|L1}, /* 9b: 0100 1101 1001 1011						*/
	{illegal2,		2, 8, 8,L0|L1}, /* 9c: 0100 1101 1001 1100						*/
	{illegal2,		2, 8, 8,L0|L1}, /* 9d: 0100 1101 1001 1101						*/
	{illegal2,		2, 8, 8,L0|L1}, /* 9e: 0100 1101 1001 1110						*/
	{illegal2,		2, 8, 8,L0|L1}, /* 9f: 0100 1101 1001 1111						*/

	{illegal2,		2, 8, 8,L0|L1}, /* a0: 0100 1101 1010 0000						*/
	{illegal2,		2, 8, 8,L0|L1}, /* a1: 0100 1101 1010 0001						*/
	{illegal2,		2, 8, 8,L0|L1}, /* a2: 0100 1101 1010 0010						*/
	{illegal2,		2, 8, 8,L0|L1}, /* a3: 0100 1101 1010 0011						*/
	{illegal2,		2, 8, 8,L0|L1}, /* a4: 0100 1101 1010 0100						*/
	{illegal2,		2, 8, 8,L0|L1}, /* a5: 0100 1101 1010 0101						*/
	{illegal2,		2, 8, 8,L0|L1}, /* a6: 0100 1101 1010 0110						*/
	{illegal2,		2, 8, 8,L0|L1}, /* a7: 0100 1101 1010 0111						*/
	{illegal2,		2, 8, 8,L0|L1}, /* a8: 0100 1101 1010 1000						*/
	{illegal2,		2, 8, 8,L0|L1}, /* a9: 0100 1101 1010 1001						*/
	{illegal2,		2, 8, 8,L0|L1}, /* aa: 0100 1101 1010 1010						*/
	{illegal2,		2, 8, 8,L0|L1}, /* ab: 0100 1101 1010 1011						*/
	{illegal2,		2, 8, 8,L0|L1}, /* ac: 0100 1101 1010 1100						*/
	{illegal2,		2, 8, 8,L0|L1}, /* ad: 0100 1101 1010 1101						*/
	{illegal2,		2, 8, 8,L0|L1}, /* ae: 0100 1101 1010 1110						*/
	{illegal2,		2, 8, 8,L0|L1}, /* af: 0100 1101 1010 1111						*/

	{illegal2,		2, 8, 8,L0|L1}, /* b0: 0100 1101 1011 0000						*/
	{illegal2,		2, 8, 8,L0|L1}, /* b1: 0100 1101 1011 0001						*/
	{illegal2,		2, 8, 8,L0|L1}, /* b2: 0100 1101 1011 0010						*/
	{illegal2,		2, 8, 8,L0|L1}, /* b3: 0100 1101 1011 0011						*/
	{illegal2,		2, 8, 8,L0|L1}, /* b4: 0100 1101 1011 0100						*/
	{illegal2,		2, 8, 8,L0|L1}, /* b5: 0100 1101 1011 0101						*/
	{illegal2,		2, 8, 8,L0|L1}, /* b6: 0100 1101 1011 0110						*/
	{illegal2,		2, 8, 8,L0|L1}, /* b7: 0100 1101 1011 0111						*/
	{illegal2,		2, 8, 8,L0|L1}, /* b8: 0100 1101 1011 1000						*/
	{illegal2,		2, 8, 8,L0|L1}, /* b9: 0100 1101 1011 1001						*/
	{illegal2,		2, 8, 8,L0|L1}, /* ba: 0100 1101 1011 1010						*/
	{illegal2,		2, 8, 8,L0|L1}, /* bb: 0100 1101 1011 1011						*/
	{illegal2,		2, 8, 8,L0|L1}, /* bc: 0100 1101 1011 1100						*/
	{illegal2,		2, 8, 8,L0|L1}, /* bd: 0100 1101 1011 1101						*/
	{illegal2,		2, 8, 8,L0|L1}, /* be: 0100 1101 1011 1110						*/
	{illegal2,		2, 8, 8,L0|L1}, /* bf: 0100 1101 1011 1111						*/

	{MOV_PA_A,		2,10,10,L0|L1}, /* c0: 0100 1101 1100 0000						*/
	{MOV_PB_A,		2,10,10,L0|L1}, /* c1: 0100 1101 1100 0001						*/
	{MOV_PC_A,		2,10,10,L0|L1}, /* c2: 0100 1101 1100 0010						*/
	{MOV_PD_A,		2,10,10,L0|L1}, /* c3: 0100 1101 1100 0011						*/
	{illegal2,		2,10,10,L0|L1}, /* c4: 0100 1101 1100 0100						*/
	{MOV_PF_A,		2,10,10,L0|L1}, /* c5: 0100 1101 1100 0101						*/
	{MOV_MKH_A, 	2,10,10,L0|L1}, /* c6: 0100 1101 1100 0110						*/
	{MOV_MKL_A, 	2,10,10,L0|L1}, /* c7: 0100 1101 1100 0111						*/
	{MOV_ANM_A, 	2,10,10,L0|L1}, /* c8: 0100 1101 1100 1000						*/
	{MOV_SMH_A, 	2,10,10,L0|L1}, /* c9: 0100 1101 1100 1001						*/
	{MOV_SML_A, 	2,10,10,L0|L1}, /* ca: 0100 1101 1100 1010						*/
	{MOV_EOM_A, 	2,10,10,L0|L1}, /* cb: 0100 1101 1100 1011						*/
	{MOV_ETMM_A,	2,10,10,L0|L1}, /* cc: 0100 1101 1100 1100						*/
	{MOV_TMM_A, 	2,10,10,L0|L1}, /* cd: 0100 1101 1100 1101						*/
	{illegal2,		2,10,10,L0|L1}, /* ce: 0100 1101 1100 1110						*/
	{illegal2,		2,10,10,L0|L1}, /* cf: 0100 1101 1100 1111						*/

	{MOV_MM_A,		2,10,10,L0|L1}, /* d0: 0100 1101 1101 0000						*/
	{MOV_MCC_A, 	2,10,10,L0|L1}, /* d1: 0100 1101 1101 0001						*/
	{MOV_MA_A,		2,10,10,L0|L1}, /* d2: 0100 1101 1101 0010						*/
	{MOV_MB_A,		2,10,10,L0|L1}, /* d3: 0100 1101 1101 0011						*/
	{MOV_MC_A,		2,10,10,L0|L1}, /* d4: 0100 1101 1101 0100						*/
	{illegal2,		2,10,10,L0|L1}, /* d5: 0100 1101 1101 0101						*/
	{illegal2,		2,10,10,L0|L1}, /* d6: 0100 1101 1101 0110						*/
	{MOV_MF_A,		2,10,10,L0|L1}, /* d7: 0100 1101 1101 0111						*/
	{MOV_TXB_A, 	2,10,10,L0|L1}, /* d8: 0100 1101 1101 1000						*/
	{illegal2,		2,10,10,L0|L1}, /* d9: 0100 1101 1101 1001						*/
	{MOV_TM0_A, 	2,10,10,L0|L1}, /* da: 0100 1101 1101 1010						*/
	{MOV_TM1_A, 	2,10,10,L0|L1}, /* db: 0100 1101 1101 1011						*/
	{illegal2,		2,10,10,L0|L1}, /* dc: 0100 1101 1101 1100						*/
	{illegal2,		2,10,10,L0|L1}, /* dd: 0100 1101 1101 1101						*/
	{illegal2,		2,10,10,L0|L1}, /* de: 0100 1101 1101 1110						*/
	{illegal2,		2,10,10,L0|L1}, /* df: 0100 1101 1101 1111						*/

	{illegal2,		2,10,10,L0|L1}, /* e0: 0100 1101 1110 0000						*/
	{illegal2,		2,10,10,L0|L1}, /* e1: 0100 1101 1110 0001						*/
	{illegal2,		2,10,10,L0|L1}, /* e2: 0100 1101 1110 0010						*/
	{illegal2,		2,10,10,L0|L1}, /* e3: 0100 1101 1110 0011						*/
	{illegal2,		2,10,10,L0|L1}, /* e4: 0100 1101 1110 0100						*/
	{illegal2,		2,10,10,L0|L1}, /* e5: 0100 1101 1110 0101						*/
	{illegal2,		2,10,10,L0|L1}, /* e6: 0100 1101 1110 0110						*/
	{illegal2,		2,10,10,L0|L1}, /* e7: 0100 1101 1110 0111						*/
	{MOV_ZCM_A, 	2,10,10,L0|L1}, /* e8: 0100 1101 1110 1000						*/
	{illegal2,		2,10,10,L0|L1}, /* e9: 0100 1101 1110 1001						*/
	{illegal2,		2,10,10,L0|L1}, /* ea: 0100 1101 1110 1010						*/
	{illegal2,		2,10,10,L0|L1}, /* eb: 0100 1101 1110 1011						*/
	{illegal2,		2,10,10,L0|L1}, /* ec: 0100 1101 1110 1100						*/
	{illegal2,		2,10,10,L0|L1}, /* ed: 0100 1101 1110 1101						*/
	{illegal2,		2,10,10,L0|L1}, /* ee: 0100 1101 1110 1110						*/
	{illegal2,		2,10,10,L0|L1}, /* ef: 0100 1101 1110 1111						*/

	{illegal2,		2,10,10,L0|L1}, /* f0: 0100 1101 1111 0000						*/
	{illegal2,		2,10,10,L0|L1}, /* f1: 0100 1101 1111 0001						*/
	{illegal2,		2,10,10,L0|L1}, /* f2: 0100 1101 1111 0010						*/
	{illegal2,		2,10,10,L0|L1}, /* f3: 0100 1101 1111 0011						*/
	{illegal2,		2,10,10,L0|L1}, /* f4: 0100 1101 1111 0100						*/
	{illegal2,		2,10,10,L0|L1}, /* f5: 0100 1101 1111 0101						*/
	{illegal2,		2,10,10,L0|L1}, /* f6: 0100 1101 1111 0110						*/
	{illegal2,		2,10,10,L0|L1}, /* f7: 0100 1101 1111 0111						*/
	{illegal2,		2,10,10,L0|L1}, /* f8: 0100 1101 1111 1000						*/
	{illegal2,		2,10,10,L0|L1}, /* f9: 0100 1101 1111 1001						*/
	{illegal2,		2,10,10,L0|L1}, /* fa: 0100 1101 1111 1010						*/
	{illegal2,		2,10,10,L0|L1}, /* fb: 0100 1101 1111 1011						*/
	{illegal2,		2,10,10,L0|L1}, /* fc: 0100 1101 1111 1100						*/
	{illegal2,		2,10,10,L0|L1}, /* fd: 0100 1101 1111 1101						*/
	{illegal2,		2,10,10,L0|L1}, /* fe: 0100 1101 1111 1110						*/
	{illegal2,		2,10,10,L0|L1}	/* ff: 0100 1101 1111 1111						*/
};

/* prefix 60 */
static struct opcode_s op60[256] =
{
	{illegal2,		2, 8, 8,L0|L1}, /* 00: 0110 0000 0000 0000						*/
	{illegal2,		2, 8, 8,L0|L1}, /* 01: 0110 0000 0000 0001						*/
	{illegal2,		2, 8, 8,L0|L1}, /* 02: 0110 0000 0000 0010						*/
	{illegal2,		2, 8, 8,L0|L1}, /* 03: 0110 0000 0000 0011						*/
	{illegal2,		2, 8, 8,L0|L1}, /* 04: 0110 0000 0000 0100						*/
	{illegal2,		2, 8, 8,L0|L1}, /* 05: 0110 0000 0000 0101						*/
	{illegal2,		2, 8, 8,L0|L1}, /* 06: 0110 0000 0000 0110						*/
	{illegal2,		2, 8, 8,L0|L1}, /* 07: 0110 0000 0000 0111						*/
	{ANA_V_A,		2, 8, 8,L0|L1}, /* 08: 0110 0000 0000 1000						*/
	{ANA_A_A,		2, 8, 8,L0|L1}, /* 09: 0110 0000 0000 1001						*/
	{ANA_B_A,		2, 8, 8,L0|L1}, /* 0a: 0110 0000 0000 1010						*/
	{ANA_C_A,		2, 8, 8,L0|L1}, /* 0b: 0110 0000 0000 1011						*/
	{ANA_D_A,		2, 8, 8,L0|L1}, /* 0c: 0110 0000 0000 1100						*/
	{ANA_E_A,		2, 8, 8,L0|L1}, /* 0d: 0110 0000 0000 1101						*/
	{ANA_H_A,		2, 8, 8,L0|L1}, /* 0e: 0110 0000 0000 1110						*/
	{ANA_L_A,		2, 8, 8,L0|L1}, /* 0f: 0110 0000 0000 1111						*/

	{XRA_V_A,		2, 8, 8,L0|L1}, /* 10: 0110 0000 0001 0000						*/
	{XRA_A_A,		2, 8, 8,L0|L1}, /* 11: 0110 0000 0001 0001						*/
	{XRA_B_A,		2, 8, 8,L0|L1}, /* 12: 0110 0000 0001 0010						*/
	{XRA_C_A,		2, 8, 8,L0|L1}, /* 13: 0110 0000 0001 0011						*/
	{XRA_D_A,		2, 8, 8,L0|L1}, /* 14: 0110 0000 0001 0100						*/
	{XRA_E_A,		2, 8, 8,L0|L1}, /* 15: 0110 0000 0001 0101						*/
	{XRA_H_A,		2, 8, 8,L0|L1}, /* 16: 0110 0000 0001 0110						*/
	{XRA_L_A,		2, 8, 8,L0|L1}, /* 17: 0110 0000 0001 0111						*/
	{ORA_V_A,		2, 8, 8,L0|L1}, /* 18: 0110 0000 0001 1000						*/
	{ORA_A_A,		2, 8, 8,L0|L1}, /* 19: 0110 0000 0001 1001						*/
	{ORA_B_A,		2, 8, 8,L0|L1}, /* 1a: 0110 0000 0001 1010						*/
	{ORA_C_A,		2, 8, 8,L0|L1}, /* 1b: 0110 0000 0001 1011						*/
	{ORA_D_A,		2, 8, 8,L0|L1}, /* 1c: 0110 0000 0001 1100						*/
	{ORA_E_A,		2, 8, 8,L0|L1}, /* 1d: 0110 0000 0001 1101						*/
	{ORA_H_A,		2, 8, 8,L0|L1}, /* 1e: 0110 0000 0001 1110						*/
	{ORA_L_A,		2, 8, 8,L0|L1}, /* 1f: 0110 0000 0001 1111						*/

	{ADDNC_V_A, 	2, 8, 8,L0|L1}, /* 20: 0110 0000 0010 0000						*/
	{ADDNC_A_A, 	2, 8, 8,L0|L1}, /* 21: 0110 0000 0010 0001						*/
	{ADDNC_B_A, 	2, 8, 8,L0|L1}, /* 22: 0110 0000 0010 0010						*/
	{ADDNC_C_A, 	2, 8, 8,L0|L1}, /* 23: 0110 0000 0010 0011						*/
	{ADDNC_D_A, 	2, 8, 8,L0|L1}, /* 24: 0110 0000 0010 0100						*/
	{ADDNC_E_A, 	2, 8, 8,L0|L1}, /* 25: 0110 0000 0010 0101						*/
	{ADDNC_H_A, 	2, 8, 8,L0|L1}, /* 26: 0110 0000 0010 0110						*/
	{ADDNC_L_A, 	2, 8, 8,L0|L1}, /* 27: 0110 0000 0010 0111						*/
	{GTA_V_A,		2, 8, 8,L0|L1}, /* 28: 0110 0000 0010 1000						*/
	{GTA_A_A,		2, 8, 8,L0|L1}, /* 29: 0110 0000 0010 1001						*/
	{GTA_B_A,		2, 8, 8,L0|L1}, /* 2a: 0110 0000 0010 1010						*/
	{GTA_C_A,		2, 8, 8,L0|L1}, /* 2b: 0110 0000 0010 1011						*/
	{GTA_D_A,		2, 8, 8,L0|L1}, /* 2c: 0110 0000 0010 1100						*/
	{GTA_E_A,		2, 8, 8,L0|L1}, /* 2d: 0110 0000 0010 1101						*/
	{GTA_H_A,		2, 8, 8,L0|L1}, /* 2e: 0110 0000 0010 1110						*/
	{GTA_L_A,		2, 8, 8,L0|L1}, /* 2f: 0110 0000 0010 1111						*/

	{SUBNB_V_A, 	2, 8, 8,L0|L1}, /* 30: 0110 0000 0011 0000						*/
	{SUBNB_A_A, 	2, 8, 8,L0|L1}, /* 31: 0110 0000 0011 0001						*/
	{SUBNB_B_A, 	2, 8, 8,L0|L1}, /* 32: 0110 0000 0011 0010						*/
	{SUBNB_C_A, 	2, 8, 8,L0|L1}, /* 33: 0110 0000 0011 0011						*/
	{SUBNB_D_A, 	2, 8, 8,L0|L1}, /* 34: 0110 0000 0011 0100						*/
	{SUBNB_E_A, 	2, 8, 8,L0|L1}, /* 35: 0110 0000 0011 0101						*/
	{SUBNB_H_A, 	2, 8, 8,L0|L1}, /* 36: 0110 0000 0011 0110						*/
	{SUBNB_L_A, 	2, 8, 8,L0|L1}, /* 37: 0110 0000 0011 0111						*/
	{LTA_V_A,		2, 8, 8,L0|L1}, /* 38: 0110 0000 0011 1000						*/
	{LTA_A_A,		2, 8, 8,L0|L1}, /* 39: 0110 0000 0011 1001						*/
	{LTA_B_A,		2, 8, 8,L0|L1}, /* 3a: 0110 0000 0011 1010						*/
	{LTA_C_A,		2, 8, 8,L0|L1}, /* 3b: 0110 0000 0011 1011						*/
	{LTA_D_A,		2, 8, 8,L0|L1}, /* 3c: 0110 0000 0011 1100						*/
	{LTA_E_A,		2, 8, 8,L0|L1}, /* 3d: 0110 0000 0011 1101						*/
	{LTA_H_A,		2, 8, 8,L0|L1}, /* 3e: 0110 0000 0011 1110						*/
	{LTA_L_A,		2, 8, 8,L0|L1}, /* 3f: 0110 0000 0011 1111						*/

	{ADD_V_A,		2, 8, 8,L0|L1}, /* 40: 0110 0000 0100 0000						*/
	{ADD_A_A,		2, 8, 8,L0|L1}, /* 41: 0110 0000 0100 0001						*/
	{ADD_B_A,		2, 8, 8,L0|L1}, /* 42: 0110 0000 0100 0010						*/
	{ADD_C_A,		2, 8, 8,L0|L1}, /* 43: 0110 0000 0100 0011						*/
	{ADD_D_A,		2, 8, 8,L0|L1}, /* 44: 0110 0000 0100 0100						*/
	{ADD_E_A,		2, 8, 8,L0|L1}, /* 45: 0110 0000 0100 0101						*/
	{ADD_H_A,		2, 8, 8,L0|L1}, /* 46: 0110 0000 0100 0110						*/
	{ADD_L_A,		2, 8, 8,L0|L1}, /* 47: 0110 0000 0100 0111						*/
	{illegal2,		2, 8, 8,L0|L1}, /* 48: 0110 0000 0100 1000						*/
	{illegal2,		2, 8, 8,L0|L1}, /* 49: 0110 0000 0100 1001						*/
	{illegal2,		2, 8, 8,L0|L1}, /* 4a: 0110 0000 0100 1010						*/
	{illegal2,		2, 8, 8,L0|L1}, /* 4b: 0110 0000 0100 1011						*/
	{illegal2,		2, 8, 8,L0|L1}, /* 4c: 0110 0000 0100 1100						*/
	{illegal2,		2, 8, 8,L0|L1}, /* 4d: 0110 0000 0100 1101						*/
	{illegal2,		2, 8, 8,L0|L1}, /* 4e: 0110 0000 0100 1110						*/
	{illegal2,		2, 8, 8,L0|L1}, /* 4f: 0110 0000 0100 1111						*/

	{ADC_V_A,		2, 8, 8,L0|L1}, /* 50: 0110 0000 0101 0000						*/
	{ADC_A_A,		2, 8, 8,L0|L1}, /* 51: 0110 0000 0101 0001						*/
	{ADC_B_A,		2, 8, 8,L0|L1}, /* 52: 0110 0000 0101 0010						*/
	{ADC_C_A,		2, 8, 8,L0|L1}, /* 53: 0110 0000 0101 0011						*/
	{ADC_D_A,		2, 8, 8,L0|L1}, /* 54: 0110 0000 0101 0100						*/
	{ADC_E_A,		2, 8, 8,L0|L1}, /* 55: 0110 0000 0101 0101						*/
	{ADC_H_A,		2, 8, 8,L0|L1}, /* 56: 0110 0000 0101 0110						*/
	{ADC_L_A,		2, 8, 8,L0|L1}, /* 57: 0110 0000 0101 0111						*/
	{illegal2,		2, 8, 8,L0|L1}, /* 58: 0110 0000 0101 1000						*/
	{illegal2,		2, 8, 8,L0|L1}, /* 59: 0110 0000 0101 1001						*/
	{illegal2,		2, 8, 8,L0|L1}, /* 5a: 0110 0000 0101 1010						*/
	{illegal2,		2, 8, 8,L0|L1}, /* 5b: 0110 0000 0101 1011						*/
	{illegal2,		2, 8, 8,L0|L1}, /* 5c: 0110 0000 0101 1100						*/
	{illegal2,		2, 8, 8,L0|L1}, /* 5d: 0110 0000 0101 1101						*/
	{illegal2,		2, 8, 8,L0|L1}, /* 5e: 0110 0000 0101 1110						*/
	{illegal2,		2, 8, 8,L0|L1}, /* 5f: 0110 0000 0101 1111						*/

	{SUB_V_A,		2, 8, 8,L0|L1}, /* 60: 0110 0000 0110 0000						*/
	{SUB_A_A,		2, 8, 8,L0|L1}, /* 61: 0110 0000 0110 0001						*/
	{SUB_B_A,		2, 8, 8,L0|L1}, /* 62: 0110 0000 0110 0010						*/
	{SUB_C_A,		2, 8, 8,L0|L1}, /* 63: 0110 0000 0110 0011						*/
	{SUB_D_A,		2, 8, 8,L0|L1}, /* 64: 0110 0000 0110 0100						*/
	{SUB_E_A,		2, 8, 8,L0|L1}, /* 65: 0110 0000 0110 0101						*/
	{SUB_H_A,		2, 8, 8,L0|L1}, /* 66: 0110 0000 0110 0110						*/
	{SUB_L_A,		2, 8, 8,L0|L1}, /* 67: 0110 0000 0110 0111						*/
	{NEA_V_A,		2, 8, 8,L0|L1}, /* 68: 0110 0000 0110 1000						*/
	{NEA_A_A,		2, 8, 8,L0|L1}, /* 69: 0110 0000 0110 1001						*/
	{NEA_B_A,		2, 8, 8,L0|L1}, /* 6a: 0110 0000 0110 1010						*/
	{NEA_C_A,		2, 8, 8,L0|L1}, /* 6b: 0110 0000 0110 1011						*/
	{NEA_D_A,		2, 8, 8,L0|L1}, /* 6c: 0110 0000 0110 1100						*/
	{NEA_E_A,		2, 8, 8,L0|L1}, /* 6d: 0110 0000 0110 1101						*/
	{NEA_H_A,		2, 8, 8,L0|L1}, /* 6e: 0110 0000 0110 1110						*/
	{NEA_L_A,		2, 8, 8,L0|L1}, /* 6f: 0110 0000 0110 1111						*/

	{SBB_V_A,		2, 8, 8,L0|L1}, /* 70: 0110 0000 0111 0000						*/
	{SBB_A_A,		2, 8, 8,L0|L1}, /* 71: 0110 0000 0111 0001						*/
	{SBB_B_A,		2, 8, 8,L0|L1}, /* 72: 0110 0000 0111 0010						*/
	{SBB_C_A,		2, 8, 8,L0|L1}, /* 73: 0110 0000 0111 0011						*/
	{SBB_D_A,		2, 8, 8,L0|L1}, /* 74: 0110 0000 0111 0100						*/
	{SBB_E_A,		2, 8, 8,L0|L1}, /* 75: 0110 0000 0111 0101						*/
	{SBB_H_A,		2, 8, 8,L0|L1}, /* 76: 0110 0000 0111 0110						*/
	{SBB_L_A,		2, 8, 8,L0|L1}, /* 77: 0110 0000 0111 0111						*/
	{EQA_V_A,		2, 8, 8,L0|L1}, /* 78: 0110 0000 0111 1000						*/
	{EQA_A_A,		2, 8, 8,L0|L1}, /* 79: 0110 0000 0111 1001						*/
	{EQA_B_A,		2, 8, 8,L0|L1}, /* 7a: 0110 0000 0111 1010						*/
	{EQA_C_A,		2, 8, 8,L0|L1}, /* 7b: 0110 0000 0111 1011						*/
	{EQA_D_A,		2, 8, 8,L0|L1}, /* 7c: 0110 0000 0111 1100						*/
	{EQA_E_A,		2, 8, 8,L0|L1}, /* 7d: 0110 0000 0111 1101						*/
	{EQA_H_A,		2, 8, 8,L0|L1}, /* 7e: 0110 0000 0111 1110						*/
	{EQA_L_A,		2, 8, 8,L0|L1}, /* 7f: 0110 0000 0111 1111						*/

	{illegal2,		2, 8, 8,L0|L1}, /* 80: 0110 0000 1000 0000						*/
	{illegal2,		2, 8, 8,L0|L1}, /* 81: 0110 0000 1000 0001						*/
	{illegal2,		2, 8, 8,L0|L1}, /* 82: 0110 0000 1000 0010						*/
	{illegal2,		2, 8, 8,L0|L1}, /* 83: 0110 0000 1000 0011						*/
	{illegal2,		2, 8, 8,L0|L1}, /* 84: 0110 0000 1000 0100						*/
	{illegal2,		2, 8, 8,L0|L1}, /* 85: 0110 0000 1000 0101						*/
	{illegal2,		2, 8, 8,L0|L1}, /* 86: 0110 0000 1000 0110						*/
	{illegal2,		2, 8, 8,L0|L1}, /* 87: 0110 0000 1000 0111						*/
	{ANA_A_V,		2, 8, 8,L0|L1}, /* 88: 0110 0000 1000 1000						*/
	{ANA_A_A,		2, 8, 8,L0|L1}, /* 89: 0110 0000 1000 1001						*/
	{ANA_A_B,		2, 8, 8,L0|L1}, /* 8a: 0110 0000 1000 1010						*/
	{ANA_A_C,		2, 8, 8,L0|L1}, /* 8b: 0110 0000 1000 1011						*/
	{ANA_A_D,		2, 8, 8,L0|L1}, /* 8c: 0110 0000 1000 1100						*/
	{ANA_A_E,		2, 8, 8,L0|L1}, /* 8d: 0110 0000 1000 1101						*/
	{ANA_A_H,		2, 8, 8,L0|L1}, /* 8e: 0110 0000 1000 1110						*/
	{ANA_A_L,		2, 8, 8,L0|L1}, /* 8f: 0110 0000 1000 1111						*/

	{XRA_A_V,		2, 8, 8,L0|L1}, /* 90: 0110 0000 1001 0000						*/
	{XRA_A_A,		2, 8, 8,L0|L1}, /* 91: 0110 0000 1001 0001						*/
	{XRA_A_B,		2, 8, 8,L0|L1}, /* 92: 0110 0000 1001 0010						*/
	{XRA_A_C,		2, 8, 8,L0|L1}, /* 93: 0110 0000 1001 0011						*/
	{XRA_A_D,		2, 8, 8,L0|L1}, /* 94: 0110 0000 1001 0100						*/
	{XRA_A_E,		2, 8, 8,L0|L1}, /* 95: 0110 0000 1001 0101						*/
	{XRA_A_H,		2, 8, 8,L0|L1}, /* 96: 0110 0000 1001 0110						*/
	{XRA_A_L,		2, 8, 8,L0|L1}, /* 97: 0110 0000 1001 0111						*/
	{ORA_A_V,		2, 8, 8,L0|L1}, /* 98: 0110 0000 1001 1000						*/
	{ORA_A_A,		2, 8, 8,L0|L1}, /* 99: 0110 0000 1001 1001						*/
	{ORA_A_B,		2, 8, 8,L0|L1}, /* 9a: 0110 0000 1001 1010						*/
	{ORA_A_C,		2, 8, 8,L0|L1}, /* 9b: 0110 0000 1001 1011						*/
	{ORA_A_D,		2, 8, 8,L0|L1}, /* 9c: 0110 0000 1001 1100						*/
	{ORA_A_E,		2, 8, 8,L0|L1}, /* 9d: 0110 0000 1001 1101						*/
	{ORA_A_H,		2, 8, 8,L0|L1}, /* 9e: 0110 0000 1001 1110						*/
	{ORA_A_L,		2, 8, 8,L0|L1}, /* 9f: 0110 0000 1001 1111						*/

	{ADDNC_A_V, 	2, 8, 8,L0|L1}, /* a0: 0110 0000 1010 0000						*/
	{ADDNC_A_A, 	2, 8, 8,L0|L1}, /* a1: 0110 0000 1010 0001						*/
	{ADDNC_A_B, 	2, 8, 8,L0|L1}, /* a2: 0110 0000 1010 0010						*/
	{ADDNC_A_C, 	2, 8, 8,L0|L1}, /* a3: 0110 0000 1010 0011						*/
	{ADDNC_A_D, 	2, 8, 8,L0|L1}, /* a4: 0110 0000 1010 0100						*/
	{ADDNC_A_E, 	2, 8, 8,L0|L1}, /* a5: 0110 0000 1010 0101						*/
	{ADDNC_A_H, 	2, 8, 8,L0|L1}, /* a6: 0110 0000 1010 0110						*/
	{ADDNC_A_L, 	2, 8, 8,L0|L1}, /* a7: 0110 0000 1010 0111						*/
	{GTA_A_V,		2, 8, 8,L0|L1}, /* a8: 0110 0000 1010 1000						*/
	{GTA_A_A,		2, 8, 8,L0|L1}, /* a9: 0110 0000 1010 1001						*/
	{GTA_A_B,		2, 8, 8,L0|L1}, /* aa: 0110 0000 1010 1010						*/
	{GTA_A_C,		2, 8, 8,L0|L1}, /* ab: 0110 0000 1010 1011						*/
	{GTA_A_D,		2, 8, 8,L0|L1}, /* ac: 0110 0000 1010 1100						*/
	{GTA_A_E,		2, 8, 8,L0|L1}, /* ad: 0110 0000 1010 1101						*/
	{GTA_A_H,		2, 8, 8,L0|L1}, /* ae: 0110 0000 1010 1110						*/
	{GTA_A_L,		2, 8, 8,L0|L1}, /* af: 0110 0000 1010 1111						*/

	{SUBNB_A_V, 	2, 8, 8,L0|L1}, /* b0: 0110 0000 1011 0000						*/
	{SUBNB_A_A, 	2, 8, 8,L0|L1}, /* b1: 0110 0000 1011 0001						*/
	{SUBNB_A_B, 	2, 8, 8,L0|L1}, /* b2: 0110 0000 1011 0010						*/
	{SUBNB_A_C, 	2, 8, 8,L0|L1}, /* b3: 0110 0000 1011 0011						*/
	{SUBNB_A_D, 	2, 8, 8,L0|L1}, /* b4: 0110 0000 1011 0100						*/
	{SUBNB_A_E, 	2, 8, 8,L0|L1}, /* b5: 0110 0000 1011 0101						*/
	{SUBNB_A_H, 	2, 8, 8,L0|L1}, /* b6: 0110 0000 1011 0110						*/
	{SUBNB_A_L, 	2, 8, 8,L0|L1}, /* b7: 0110 0000 1011 0111						*/
	{LTA_A_V,		2, 8, 8,L0|L1}, /* b8: 0110 0000 1011 1000						*/
	{LTA_A_A,		2, 8, 8,L0|L1}, /* b9: 0110 0000 1011 1001						*/
	{LTA_A_B,		2, 8, 8,L0|L1}, /* ba: 0110 0000 1011 1010						*/
	{LTA_A_C,		2, 8, 8,L0|L1}, /* bb: 0110 0000 1011 1011						*/
	{LTA_A_D,		2, 8, 8,L0|L1}, /* bc: 0110 0000 1011 1100						*/
	{LTA_A_E,		2, 8, 8,L0|L1}, /* bd: 0110 0000 1011 1101						*/
	{LTA_A_H,		2, 8, 8,L0|L1}, /* be: 0110 0000 1011 1110						*/
	{LTA_A_L,		2, 8, 8,L0|L1}, /* bf: 0110 0000 1011 1111						*/

	{ADD_A_V,		2, 8, 8,L0|L1}, /* c0: 0110 0000 1100 0000						*/
	{ADD_A_A,		2, 8, 8,L0|L1}, /* c1: 0110 0000 1100 0001						*/
	{ADD_A_B,		2, 8, 8,L0|L1}, /* c2: 0110 0000 1100 0010						*/
	{ADD_A_C,		2, 8, 8,L0|L1}, /* c3: 0110 0000 1100 0011						*/
	{ADD_A_D,		2, 8, 8,L0|L1}, /* c4: 0110 0000 1100 0100						*/
	{ADD_A_E,		2, 8, 8,L0|L1}, /* c5: 0110 0000 1100 0101						*/
	{ADD_A_H,		2, 8, 8,L0|L1}, /* c6: 0110 0000 1100 0110						*/
	{ADD_A_L,		2, 8, 8,L0|L1}, /* c7: 0110 0000 1100 0111						*/
	{ONA_A_V,		2, 8, 8,L0|L1}, /* c8: 0110 0000 1100 1000						*/
	{ONA_A_A,		2, 8, 8,L0|L1}, /* c9: 0110 0000 1100 1001						*/
	{ONA_A_B,		2, 8, 8,L0|L1}, /* ca: 0110 0000 1100 1010						*/
	{ONA_A_C,		2, 8, 8,L0|L1}, /* cb: 0110 0000 1100 1011						*/
	{ONA_A_D,		2, 8, 8,L0|L1}, /* cc: 0110 0000 1100 1100						*/
	{ONA_A_E,		2, 8, 8,L0|L1}, /* cd: 0110 0000 1100 1101						*/
	{ONA_A_H,		2, 8, 8,L0|L1}, /* ce: 0110 0000 1100 1110						*/
	{ONA_A_L,		2, 8, 8,L0|L1}, /* cf: 0110 0000 1100 1111						*/

	{ADC_A_V,		2, 8, 8,L0|L1}, /* d0: 0110 0000 1101 0000						*/
	{ADC_A_A,		2, 8, 8,L0|L1}, /* d1: 0110 0000 1101 0001						*/
	{ADC_A_B,		2, 8, 8,L0|L1}, /* d2: 0110 0000 1101 0010						*/
	{ADC_A_C,		2, 8, 8,L0|L1}, /* d3: 0110 0000 1101 0011						*/
	{ADC_A_D,		2, 8, 8,L0|L1}, /* d4: 0110 0000 1101 0100						*/
	{ADC_A_E,		2, 8, 8,L0|L1}, /* d5: 0110 0000 1101 0101						*/
	{ADC_A_H,		2, 8, 8,L0|L1}, /* d6: 0110 0000 1101 0110						*/
	{ADC_A_L,		2, 8, 8,L0|L1}, /* d7: 0110 0000 1101 0111						*/
	{OFFA_A_V,		2, 8, 8,L0|L1}, /* d8: 0110 0000 1101 1000						*/
	{OFFA_A_A,		2, 8, 8,L0|L1}, /* d9: 0110 0000 1101 1001						*/
	{OFFA_A_B,		2, 8, 8,L0|L1}, /* da: 0110 0000 1101 1010						*/
	{OFFA_A_C,		2, 8, 8,L0|L1}, /* db: 0110 0000 1101 1011						*/
	{OFFA_A_D,		2, 8, 8,L0|L1}, /* dc: 0110 0000 1101 1100						*/
	{OFFA_A_E,		2, 8, 8,L0|L1}, /* dd: 0110 0000 1101 1101						*/
	{OFFA_A_H,		2, 8, 8,L0|L1}, /* de: 0110 0000 1101 1110						*/
	{OFFA_A_L,		2, 8, 8,L0|L1}, /* df: 0110 0000 1101 1111						*/

	{SUB_A_V,		2, 8, 8,L0|L1}, /* e0: 0110 0000 1110 0000						*/
	{SUB_A_A,		2, 8, 8,L0|L1}, /* e1: 0110 0000 1110 0001						*/
	{SUB_A_B,		2, 8, 8,L0|L1}, /* e2: 0110 0000 1110 0010						*/
	{SUB_A_C,		2, 8, 8,L0|L1}, /* e3: 0110 0000 1110 0011						*/
	{SUB_A_D,		2, 8, 8,L0|L1}, /* e4: 0110 0000 1110 0100						*/
	{SUB_A_E,		2, 8, 8,L0|L1}, /* e5: 0110 0000 1110 0101						*/
	{SUB_A_H,		2, 8, 8,L0|L1}, /* e6: 0110 0000 1110 0110						*/
	{SUB_A_L,		2, 8, 8,L0|L1}, /* e7: 0110 0000 1110 0111						*/
	{NEA_A_V,		2, 8, 8,L0|L1}, /* e8: 0110 0000 1110 1000						*/
	{NEA_A_A,		2, 8, 8,L0|L1}, /* e9: 0110 0000 1110 1001						*/
	{NEA_A_B,		2, 8, 8,L0|L1}, /* ea: 0110 0000 1110 1010						*/
	{NEA_A_C,		2, 8, 8,L0|L1}, /* eb: 0110 0000 1110 1011						*/
	{NEA_A_D,		2, 8, 8,L0|L1}, /* ec: 0110 0000 1110 1100						*/
	{NEA_A_E,		2, 8, 8,L0|L1}, /* ed: 0110 0000 1110 1101						*/
	{NEA_A_H,		2, 8, 8,L0|L1}, /* ee: 0110 0000 1110 1110						*/
	{NEA_A_L,		2, 8, 8,L0|L1}, /* ef: 0110 0000 1110 1111						*/

	{SBB_A_V,		2, 8, 8,L0|L1}, /* f0: 0110 0000 1111 0000						*/
	{SBB_A_A,		2, 8, 8,L0|L1}, /* f1: 0110 0000 1111 0001						*/
	{SBB_A_B,		2, 8, 8,L0|L1}, /* f2: 0110 0000 1111 0010						*/
	{SBB_A_C,		2, 8, 8,L0|L1}, /* f3: 0110 0000 1111 0011						*/
	{SBB_A_D,		2, 8, 8,L0|L1}, /* f4: 0110 0000 1111 0100						*/
	{SBB_A_E,		2, 8, 8,L0|L1}, /* f5: 0110 0000 1111 0101						*/
	{SBB_A_H,		2, 8, 8,L0|L1}, /* f6: 0110 0000 1111 0110						*/
	{SBB_A_L,		2, 8, 8,L0|L1}, /* f7: 0110 0000 1111 0111						*/
	{EQA_A_V,		2, 8, 8,L0|L1}, /* f8: 0110 0000 1111 1000						*/
	{EQA_A_A,		2, 8, 8,L0|L1}, /* f9: 0110 0000 1111 1001						*/
	{EQA_A_B,		2, 8, 8,L0|L1}, /* fa: 0110 0000 1111 1010						*/
	{EQA_A_C,		2, 8, 8,L0|L1}, /* fb: 0110 0000 1111 1011						*/
	{EQA_A_D,		2, 8, 8,L0|L1}, /* fc: 0110 0000 1111 1100						*/
	{EQA_A_E,		2, 8, 8,L0|L1}, /* fd: 0110 0000 1111 1101						*/
	{EQA_A_H,		2, 8, 8,L0|L1}, /* fe: 0110 0000 1111 1110						*/
	{EQA_A_L,		2, 8, 8,L0|L1}	/* ff: 0110 0000 1111 1111						*/
};

/* prefix 64 */
static struct opcode_s op64[256] =
{
	{MVI_PA_xx, 	3,14,11,L0|L1}, /* 00: 0110 0100 0000 0000 xxxx xxxx			*/
	{MVI_PB_xx, 	3,14,11,L0|L1}, /* 01: 0110 0100 0000 0001 xxxx xxxx			*/
	{MVI_PC_xx, 	3,14,11,L0|L1}, /* 02: 0110 0100 0000 0010 xxxx xxxx			*/
	{MVI_PD_xx, 	3,14,11,L0|L1}, /* 03: 0110 0100 0000 0011 xxxx xxxx			*/
	{illegal2,		3,11,11,L0|L1}, /* 04: 0110 0100 0000 0100 xxxx xxxx			*/
	{MVI_PF_xx, 	3,14,11,L0|L1}, /* 05: 0110 0100 0000 0101 xxxx xxxx			*/
	{MVI_MKH_xx,	3,14,11,L0|L1}, /* 06: 0110 0100 0000 0110 xxxx xxxx			*/
	{MVI_MKL_xx,	3,14,11,L0|L1}, /* 07: 0110 0100 0000 0111 xxxx xxxx			*/
	{ANI_PA_xx, 	3,20,11,L0|L1}, /* 08: 0110 0100 0000 1000 xxxx xxxx			*/
	{ANI_PB_xx, 	3,20,11,L0|L1}, /* 09: 0110 0100 0000 1001 xxxx xxxx			*/
	{ANI_PC_xx, 	3,20,11,L0|L1}, /* 0a: 0110 0100 0000 1010 xxxx xxxx			*/
	{ANI_PD_xx, 	3,20,11,L0|L1}, /* 0b: 0110 0100 0000 1011 xxxx xxxx			*/
	{illegal2,		3,11,11,L0|L1}, /* 0c: 0110 0100 0000 1100 xxxx xxxx			*/
	{ANI_PF_xx, 	3,20,11,L0|L1}, /* 0d: 0110 0100 0000 1101 xxxx xxxx			*/
	{ANI_MKH_xx,	3,20,11,L0|L1}, /* 0e: 0110 0100 0000 1110 xxxx xxxx			*/
	{ANI_MKL_xx,	3,20,11,L0|L1}, /* 0f: 0110 0100 0000 1111 xxxx xxxx			*/

	{XRI_PA_xx, 	3,20,11,L0|L1}, /* 10: 0110 0100 0001 0000 xxxx xxxx			*/
	{XRI_PB_xx, 	3,20,11,L0|L1}, /* 11: 0110 0100 0001 0001 xxxx xxxx			*/
	{XRI_PC_xx, 	3,20,11,L0|L1}, /* 12: 0110 0100 0001 0010 xxxx xxxx			*/
	{XRI_PD_xx, 	3,20,11,L0|L1}, /* 13: 0110 0100 0001 0011 xxxx xxxx			*/
	{illegal2,		3,11,11,L0|L1}, /* 14: 0110 0100 0001 0100 xxxx xxxx			*/
	{XRI_PF_xx, 	3,20,11,L0|L1}, /* 15: 0110 0100 0001 0101 xxxx xxxx			*/
	{XRI_MKH_xx,	3,20,11,L0|L1}, /* 16: 0110 0100 0001 0110 xxxx xxxx			*/
	{XRI_MKL_xx,	3,20,11,L0|L1}, /* 17: 0110 0100 0001 0111 xxxx xxxx			*/
	{ORI_PA_xx, 	3,20,11,L0|L1}, /* 18: 0110 0100 0001 1000 xxxx xxxx			*/
	{ORI_PB_xx, 	3,20,11,L0|L1}, /* 19: 0110 0100 0001 1001 xxxx xxxx			*/
	{ORI_PC_xx, 	3,20,11,L0|L1}, /* 1a: 0110 0100 0001 1010 xxxx xxxx			*/
	{ORI_PD_xx, 	3,20,11,L0|L1}, /* 1b: 0110 0100 0001 1011 xxxx xxxx			*/
	{illegal2,		3,11,11,L0|L1}, /* 1c: 0110 0100 0001 1100 xxxx xxxx			*/
	{ORI_PF_xx, 	3,20,11,L0|L1}, /* 1d: 0110 0100 0001 1101 xxxx xxxx			*/
	{ORI_MKH_xx,	3,20,11,L0|L1}, /* 1e: 0110 0100 0001 1110 xxxx xxxx			*/
	{ORI_MKL_xx,	3,20,11,L0|L1}, /* 1f: 0110 0100 0001 1111 xxxx xxxx			*/

	{ADINC_PA_xx,	3,20,11,L0|L1}, /* 20: 0110 0100 0010 0000 xxxx xxxx			*/
	{ADINC_PB_xx,	3,20,11,L0|L1}, /* 21: 0110 0100 0010 0001 xxxx xxxx			*/
	{ADINC_PC_xx,	3,20,11,L0|L1}, /* 22: 0110 0100 0010 0010 xxxx xxxx			*/
	{ADINC_PD_xx,	3,20,11,L0|L1}, /* 23: 0110 0100 0010 0011 xxxx xxxx			*/
	{illegal2,		3,11,11,L0|L1}, /* 24: 0110 0100 0010 0100 xxxx xxxx			*/
	{ADINC_PF_xx,	3,20,11,L0|L1}, /* 25: 0110 0100 0010 0101 xxxx xxxx			*/
	{ADINC_MKH_xx,	3,20,11,L0|L1}, /* 26: 0110 0100 0010 0110 xxxx xxxx			*/
	{ADINC_MKL_xx,	3,20,11,L0|L1}, /* 27: 0110 0100 0010 0111 xxxx xxxx			*/
	{GTI_PA_xx, 	3,20,11,L0|L1}, /* 28: 0110 0100 0010 1000 xxxx xxxx			*/
	{GTI_PB_xx, 	3,20,11,L0|L1}, /* 29: 0110 0100 0010 1001 xxxx xxxx			*/
	{GTI_PC_xx, 	3,20,11,L0|L1}, /* 2a: 0110 0100 0010 1010 xxxx xxxx			*/
	{GTI_PD_xx, 	3,20,11,L0|L1}, /* 2b: 0110 0100 0010 1011 xxxx xxxx			*/
	{illegal2,		3,11,11,L0|L1}, /* 2c: 0110 0100 0010 1100 xxxx xxxx			*/
	{GTI_PF_xx, 	3,20,11,L0|L1}, /* 2d: 0110 0100 0010 1101 xxxx xxxx			*/
	{GTI_MKH_xx,	3,20,11,L0|L1}, /* 2e: 0110 0100 0010 1110 xxxx xxxx			*/
	{GTI_MKL_xx,	3,20,11,L0|L1}, /* 2f: 0110 0100 0010 1111 xxxx xxxx			*/

	{SUINB_PA_xx,	3,20,11,L0|L1}, /* 30: 0110 0100 0011 0000 xxxx xxxx			*/
	{SUINB_PB_xx,	3,20,11,L0|L1}, /* 31: 0110 0100 0011 0001 xxxx xxxx			*/
	{SUINB_PC_xx,	3,20,11,L0|L1}, /* 32: 0110 0100 0011 0010 xxxx xxxx			*/
	{SUINB_PD_xx,	3,20,11,L0|L1}, /* 33: 0110 0100 0011 0011 xxxx xxxx			*/
	{illegal2,		3,11,11,L0|L1}, /* 34: 0110 0100 0011 0100 xxxx xxxx			*/
	{SUINB_PF_xx,	3,20,11,L0|L1}, /* 35: 0110 0100 0011 0101 xxxx xxxx			*/
	{SUINB_MKH_xx,	3,20,11,L0|L1}, /* 36: 0110 0100 0011 0110 xxxx xxxx			*/
	{SUINB_MKL_xx,	3,20,11,L0|L1}, /* 37: 0110 0100 0011 0111 xxxx xxxx			*/
	{LTI_PA_xx, 	3,20,11,L0|L1}, /* 38: 0110 0100 0011 1000 xxxx xxxx			*/
	{LTI_PB_xx, 	3,20,11,L0|L1}, /* 39: 0110 0100 0011 1001 xxxx xxxx			*/
	{LTI_PC_xx, 	3,20,11,L0|L1}, /* 3a: 0110 0100 0011 1010 xxxx xxxx			*/
	{LTI_PD_xx, 	3,20,11,L0|L1}, /* 3b: 0110 0100 0011 1011 xxxx xxxx			*/
	{illegal2,		3,11,11,L0|L1}, /* 3c: 0110 0100 0011 1100 xxxx xxxx			*/
	{LTI_PF_xx, 	3,20,11,L0|L1}, /* 3d: 0110 0100 0011 1101 xxxx xxxx			*/
	{LTI_MKH_xx,	3,20,11,L0|L1}, /* 3e: 0110 0100 0011 1110 xxxx xxxx			*/
	{LTI_MKL_xx,	3,20,11,L0|L1}, /* 3f: 0110 0100 0011 1111 xxxx xxxx			*/

	{ADI_PA_xx, 	3,20,11,L0|L1}, /* 40: 0110 0100 0100 0000 xxxx xxxx			*/
	{ADI_PB_xx, 	3,20,11,L0|L1}, /* 41: 0110 0100 0100 0001 xxxx xxxx			*/
	{ADI_PC_xx, 	3,20,11,L0|L1}, /* 42: 0110 0100 0100 0010 xxxx xxxx			*/
	{ADI_PD_xx, 	3,20,11,L0|L1}, /* 43: 0110 0100 0100 0011 xxxx xxxx			*/
	{illegal2,		3,11,11,L0|L1}, /* 44: 0110 0100 0100 0100 xxxx xxxx			*/
	{ADI_PF_xx, 	3,20,11,L0|L1}, /* 45: 0110 0100 0100 0101 xxxx xxxx			*/
	{ADI_MKH_xx,	3,20,11,L0|L1}, /* 46: 0110 0100 0100 0110 xxxx xxxx			*/
	{ADI_MKL_xx,	3,20,11,L0|L1}, /* 47: 0110 0100 0100 0111 xxxx xxxx			*/
	{ONI_PA_xx, 	3,20,11,L0|L1}, /* 48: 0110 0100 0100 1000 xxxx xxxx			*/
	{ONI_PB_xx, 	3,20,11,L0|L1}, /* 49: 0110 0100 0100 1001 xxxx xxxx			*/
	{ONI_PC_xx, 	3,20,11,L0|L1}, /* 4a: 0110 0100 0100 1010 xxxx xxxx			*/
	{ONI_PD_xx, 	3,20,11,L0|L1}, /* 4b: 0110 0100 0100 1011 xxxx xxxx			*/
	{illegal2,		3,11,11,L0|L1}, /* 4c: 0110 0100 0100 1100 xxxx xxxx			*/
	{ONI_PF_xx, 	3,20,11,L0|L1}, /* 4d: 0110 0100 0100 1101 xxxx xxxx			*/
	{ONI_MKH_xx,	3,20,11,L0|L1}, /* 4e: 0110 0100 0100 1110 xxxx xxxx			*/
	{ONI_MKL_xx,	3,20,11,L0|L1}, /* 4f: 0110 0100 0100 1111 xxxx xxxx			*/

	{ACI_PA_xx, 	3,20,11,L0|L1}, /* 50: 0110 0100 0101 0000 xxxx xxxx			*/
	{ACI_PB_xx, 	3,20,11,L0|L1}, /* 51: 0110 0100 0101 0001 xxxx xxxx			*/
	{ACI_PC_xx, 	3,20,11,L0|L1}, /* 52: 0110 0100 0101 0010 xxxx xxxx			*/
	{ACI_PD_xx, 	3,20,11,L0|L1}, /* 53: 0110 0100 0101 0011 xxxx xxxx			*/
	{illegal2,		3,11,11,L0|L1}, /* 54: 0110 0100 0101 0100 xxxx xxxx			*/
	{ACI_PF_xx, 	3,20,11,L0|L1}, /* 55: 0110 0100 0101 0101 xxxx xxxx			*/
	{ACI_MKH_xx,	3,20,11,L0|L1}, /* 56: 0110 0100 0101 0110 xxxx xxxx			*/
	{ACI_MKL_xx,	3,20,11,L0|L1}, /* 57: 0110 0100 0101 0111 xxxx xxxx			*/
	{OFFI_PA_xx,	3,20,11,L0|L1}, /* 58: 0110 0100 0101 1000 xxxx xxxx			*/
	{OFFI_PB_xx,	3,20,11,L0|L1}, /* 59: 0110 0100 0101 1001 xxxx xxxx			*/
	{OFFI_PC_xx,	3,20,11,L0|L1}, /* 5a: 0110 0100 0101 1010 xxxx xxxx			*/
	{OFFI_PD_xx,	3,20,11,L0|L1}, /* 5b: 0110 0100 0101 1011 xxxx xxxx			*/
	{illegal2,		3,11,11,L0|L1}, /* 5c: 0110 0100 0101 1100 xxxx xxxx			*/
	{OFFI_PF_xx,	3,20,11,L0|L1}, /* 5d: 0110 0100 0101 1101 xxxx xxxx			*/
	{OFFI_MKH_xx,	3,20,11,L0|L1}, /* 5e: 0110 0100 0101 1110 xxxx xxxx			*/
	{OFFI_MKL_xx,	3,20,11,L0|L1}, /* 5f: 0110 0100 0101 1111 xxxx xxxx			*/

	{SUI_PA_xx, 	3,20,11,L0|L1}, /* 60: 0110 0100 0110 0000 xxxx xxxx			*/
	{SUI_PB_xx, 	3,20,11,L0|L1}, /* 61: 0110 0100 0110 0001 xxxx xxxx			*/
	{SUI_PC_xx, 	3,20,11,L0|L1}, /* 62: 0110 0100 0110 0010 xxxx xxxx			*/
	{SUI_PD_xx, 	3,20,11,L0|L1}, /* 63: 0110 0100 0110 0011 xxxx xxxx			*/
	{illegal2,		3,11,11,L0|L1}, /* 64: 0110 0100 0110 0100 xxxx xxxx			*/
	{SUI_PF_xx, 	3,20,11,L0|L1}, /* 65: 0110 0100 0110 0101 xxxx xxxx			*/
	{SUI_MKH_xx,	3,20,11,L0|L1}, /* 66: 0110 0100 0110 0110 xxxx xxxx			*/
	{SUI_MKL_xx,	3,20,11,L0|L1}, /* 67: 0110 0100 0110 0111 xxxx xxxx			*/
	{NEI_PA_xx, 	3,20,11,L0|L1}, /* 68: 0110 0100 0110 1000 xxxx xxxx			*/
	{NEI_PB_xx, 	3,20,11,L0|L1}, /* 69: 0110 0100 0110 1001 xxxx xxxx			*/
	{NEI_PC_xx, 	3,20,11,L0|L1}, /* 6a: 0110 0100 0110 1010 xxxx xxxx			*/
	{NEI_PD_xx, 	3,20,11,L0|L1}, /* 6b: 0110 0100 0110 1011 xxxx xxxx			*/
	{illegal2,		3,11,11,L0|L1}, /* 6c: 0110 0100 0110 1100 xxxx xxxx			*/
	{NEI_PF_xx, 	3,20,11,L0|L1}, /* 6d: 0110 0100 0110 1101 xxxx xxxx			*/
	{NEI_MKH_xx,	3,20,11,L0|L1}, /* 6e: 0110 0100 0110 1110 xxxx xxxx			*/
	{NEI_MKL_xx,	3,20,11,L0|L1}, /* 6f: 0110 0100 0110 1111 xxxx xxxx			*/

	{SBI_PA_xx, 	3,20,11,L0|L1}, /* 70: 0110 0100 0111 0000 xxxx xxxx			*/
	{SBI_PB_xx, 	3,20,11,L0|L1}, /* 71: 0110 0100 0111 0001 xxxx xxxx			*/
	{SBI_PC_xx, 	3,20,11,L0|L1}, /* 72: 0110 0100 0111 0010 xxxx xxxx			*/
	{SBI_PD_xx, 	3,20,11,L0|L1}, /* 73: 0110 0100 0111 0011 xxxx xxxx			*/
	{illegal2,		3,11,11,L0|L1}, /* 74: 0110 0100 0111 0100 xxxx xxxx			*/
	{SBI_PF_xx, 	3,20,11,L0|L1}, /* 75: 0110 0100 0111 0101 xxxx xxxx			*/
	{SBI_MKH_xx,	3,20,11,L0|L1}, /* 76: 0110 0100 0111 0110 xxxx xxxx			*/
	{SBI_MKL_xx,	3,20,11,L0|L1}, /* 77: 0110 0100 0111 0111 xxxx xxxx			*/
	{EQI_PA_xx, 	3,20,11,L0|L1}, /* 78: 0110 0100 0111 1000 xxxx xxxx			*/
	{EQI_PB_xx, 	3,20,11,L0|L1}, /* 79: 0110 0100 0111 1001 xxxx xxxx			*/
	{EQI_PC_xx, 	3,20,11,L0|L1}, /* 7a: 0110 0100 0111 1010 xxxx xxxx			*/
	{EQI_PD_xx, 	3,20,11,L0|L1}, /* 7b: 0110 0100 0111 1011 xxxx xxxx			*/
	{illegal2,		3,11,11,L0|L1}, /* 7c: 0110 0100 0111 1100 xxxx xxxx			*/
	{EQI_PF_xx, 	3,20,11,L0|L1}, /* 7d: 0110 0100 0111 1101 xxxx xxxx			*/
	{EQI_MKH_xx,	3,20,11,L0|L1}, /* 7e: 0110 0100 0111 1110 xxxx xxxx			*/
	{EQI_MKL_xx,	3,20,11,L0|L1}, /* 7f: 0110 0100 0111 1111 xxxx xxxx			*/

	{MVI_ANM_xx,	3,14,11,L0|L1}, /* 80: 0110 0100 1000 0000 xxxx xxxx			*/
	{MVI_SMH_xx,	3,14,11,L0|L1}, /* 81: 0110 0100 1000 0001 xxxx xxxx			*/
	{illegal2,		3,11,11,L0|L1}, /* 82: 0110 0100 1000 0010 xxxx xxxx			*/
	{MVI_EOM_xx,	3,14,11,L0|L1}, /* 83: 0110 0100 1000 0011 xxxx xxxx			*/
	{illegal2,		3,11,11,L0|L1}, /* 84: 0110 0100 1000 0100 xxxx xxxx			*/
	{MVI_TMM_xx,	3,14,11,L0|L1}, /* 85: 0110 0100 1000 0101 xxxx xxxx			*/
	{illegal2,		3,11,11,L0|L1}, /* 86: 0110 0100 1000 0110 xxxx xxxx			*/
	{illegal2,		3,11,11,L0|L1}, /* 87: 0110 0100 1000 0111 xxxx xxxx			*/
	{ANI_ANM_xx,	3,20,11,L0|L1}, /* 88: 0110 0100 1000 1000 xxxx xxxx			*/
	{ANI_SMH_xx,	3,20,11,L0|L1}, /* 89: 0110 0100 1000 1001 xxxx xxxx			*/
	{illegal2,		3,11,11,L0|L1}, /* 8a: 0110 0100 1000 1010 xxxx xxxx			*/
	{ANI_EOM_xx,	3,20,11,L0|L1}, /* 8b: 0110 0100 1000 1011 xxxx xxxx			*/
	{illegal2,		3,11,11,L0|L1}, /* 8c: 0110 0100 1000 1100 xxxx xxxx			*/
	{ANI_TMM_xx,	3,20,11,L0|L1}, /* 8d: 0110 0100 1000 1101 xxxx xxxx			*/
	{illegal2,		3,11,11,L0|L1}, /* 8e: 0110 0100 1000 1110 xxxx xxxx			*/
	{illegal2,		3,11,11,L0|L1}, /* 8f: 0110 0100 1000 1111 xxxx xxxx			*/

	{XRI_ANM_xx,	3,20,11,L0|L1}, /* 90: 0110 0100 1001 0000 xxxx xxxx			*/
	{XRI_SMH_xx,	3,20,11,L0|L1}, /* 91: 0110 0100 1001 0001 xxxx xxxx			*/
	{illegal2,		3,11,11,L0|L1}, /* 92: 0110 0100 1001 0010 xxxx xxxx			*/
	{XRI_EOM_xx,	3,20,11,L0|L1}, /* 93: 0110 0100 1001 0011 xxxx xxxx			*/
	{illegal2,		3,11,11,L0|L1}, /* 94: 0110 0100 1001 0100 xxxx xxxx			*/
	{XRI_TMM_xx,	3,20,11,L0|L1}, /* 95: 0110 0100 1001 0101 xxxx xxxx			*/
	{illegal2,		3,11,11,L0|L1}, /* 96: 0110 0100 1001 0110 xxxx xxxx			*/
	{illegal2,		3,11,11,L0|L1}, /* 97: 0110 0100 1001 0111 xxxx xxxx			*/
	{ORI_ANM_xx,	3,20,11,L0|L1}, /* 98: 0110 0100 1001 1000 xxxx xxxx			*/
	{ORI_SMH_xx,	3,20,11,L0|L1}, /* 99: 0110 0100 1001 1001 xxxx xxxx			*/
	{illegal2,		3,11,11,L0|L1}, /* 9a: 0110 0100 1001 1010 xxxx xxxx			*/
	{ORI_EOM_xx,	3,20,11,L0|L1}, /* 9b: 0110 0100 1001 1011 xxxx xxxx			*/
	{illegal2,		3,11,11,L0|L1}, /* 9c: 0110 0100 1001 1100 xxxx xxxx			*/
	{ORI_TMM_xx,	3,20,11,L0|L1}, /* 9d: 0110 0100 1001 1101 xxxx xxxx			*/
	{illegal2,		3,11,11,L0|L1}, /* 9e: 0110 0100 1001 1110 xxxx xxxx			*/
	{illegal2,		3,11,11,L0|L1}, /* 9f: 0110 0100 1001 1111 xxxx xxxx			*/

	{ADINC_ANM_xx,	3,20,11,L0|L1}, /* a0: 0110 0100 1010 0000 xxxx xxxx			*/
	{ADINC_SMH_xx,	3,20,11,L0|L1}, /* a1: 0110 0100 1010 0001 xxxx xxxx			*/
	{illegal2,		3,11,11,L0|L1}, /* a2: 0110 0100 1010 0010 xxxx xxxx			*/
	{ADINC_EOM_xx,	3,20,11,L0|L1}, /* a3: 0110 0100 1010 0011 xxxx xxxx			*/
	{illegal2,		3,11,11,L0|L1}, /* a4: 0110 0100 1010 0100 xxxx xxxx			*/
	{ADINC_TMM_xx,	3,20,11,L0|L1}, /* a5: 0110 0100 1010 0101 xxxx xxxx			*/
	{illegal2,		3,11,11,L0|L1}, /* a6: 0110 0100 1010 0110 xxxx xxxx			*/
	{illegal2,		3,11,11,L0|L1}, /* a7: 0110 0100 1010 0111 xxxx xxxx			*/
	{GTI_ANM_xx,	3,20,11,L0|L1}, /* a8: 0110 0100 1010 1000 xxxx xxxx			*/
	{GTI_SMH_xx,	3,20,11,L0|L1}, /* a9: 0110 0100 1010 1001 xxxx xxxx			*/
	{illegal2,		3,11,11,L0|L1}, /* aa: 0110 0100 1010 1010 xxxx xxxx			*/
	{GTI_EOM_xx,	3,20,11,L0|L1}, /* ab: 0110 0100 1010 1011 xxxx xxxx			*/
	{illegal2,		3,11,11,L0|L1}, /* ac: 0110 0100 1010 1100 xxxx xxxx			*/
	{GTI_TMM_xx,	3,20,11,L0|L1}, /* ad: 0110 0100 1010 1101 xxxx xxxx			*/
	{illegal2,		3,11,11,L0|L1}, /* ae: 0110 0100 1010 1110 xxxx xxxx			*/
	{illegal2,		3,11,11,L0|L1}, /* af: 0110 0100 1010 1111 xxxx xxxx			*/

	{SUINB_ANM_xx,	3,20,11,L0|L1}, /* b0: 0110 0100 1011 0000 xxxx xxxx			*/
	{SUINB_SMH_xx,	3,20,11,L0|L1}, /* b1: 0110 0100 1011 0001 xxxx xxxx			*/
	{illegal2,		3,11,11,L0|L1}, /* b2: 0110 0100 1011 0010 xxxx xxxx			*/
	{SUINB_EOM_xx,	3,20,11,L0|L1}, /* b3: 0110 0100 1011 0011 xxxx xxxx			*/
	{illegal2,		3,11,11,L0|L1}, /* b4: 0110 0100 1011 0100 xxxx xxxx			*/
	{SUINB_TMM_xx,	3,20,11,L0|L1}, /* b5: 0110 0100 1011 0101 xxxx xxxx			*/
	{illegal2,		3,11,11,L0|L1}, /* b6: 0110 0100 1011 0110 xxxx xxxx			*/
	{illegal2,		3,11,11,L0|L1}, /* b7: 0110 0100 1011 0111 xxxx xxxx			*/
	{LTI_ANM_xx,	3,20,11,L0|L1}, /* b8: 0110 0100 1011 1000 xxxx xxxx			*/
	{LTI_SMH_xx,	3,20,11,L0|L1}, /* b9: 0110 0100 1011 1001 xxxx xxxx			*/
	{illegal2,		3,11,11,L0|L1}, /* ba: 0110 0100 1011 1010 xxxx xxxx			*/
	{LTI_EOM_xx,	3,20,11,L0|L1}, /* bb: 0110 0100 1011 1011 xxxx xxxx			*/
	{illegal2,		3,11,11,L0|L1}, /* bc: 0110 0100 1011 1100 xxxx xxxx			*/
	{LTI_TMM_xx,	3,20,11,L0|L1}, /* bd: 0110 0100 1011 1101 xxxx xxxx			*/
	{illegal2,		3,11,11,L0|L1}, /* be: 0110 0100 1011 1110 xxxx xxxx			*/
	{illegal2,		3,11,11,L0|L1}, /* bf: 0110 0100 1011 1111 xxxx xxxx			*/

	{ADI_ANM_xx,	3,20,11,L0|L1}, /* c0: 0110 0100 1100 0000 xxxx xxxx			*/
	{ADI_SMH_xx,	3,20,11,L0|L1}, /* c1: 0110 0100 1100 0001 xxxx xxxx			*/
	{illegal2,		3,11,11,L0|L1}, /* c2: 0110 0100 1100 0010 xxxx xxxx			*/
	{ADI_EOM_xx,	3,20,11,L0|L1}, /* c3: 0110 0100 1100 0011 xxxx xxxx			*/
	{illegal2,		3,11,11,L0|L1}, /* c4: 0110 0100 1100 0100 xxxx xxxx			*/
	{ADI_TMM_xx,	3,20,11,L0|L1}, /* c5: 0110 0100 1100 0101 xxxx xxxx			*/
	{illegal2,		3,11,11,L0|L1}, /* c6: 0110 0100 1100 0110 xxxx xxxx			*/
	{illegal2,		3,11,11,L0|L1}, /* c7: 0110 0100 1100 0111 xxxx xxxx			*/
	{ONI_ANM_xx,	3,20,11,L0|L1}, /* c8: 0110 0100 1100 1000 xxxx xxxx			*/
	{ONI_SMH_xx,	3,20,11,L0|L1}, /* c9: 0110 0100 1100 1001 xxxx xxxx			*/
	{illegal2,		3,11,11,L0|L1}, /* ca: 0110 0100 1100 1010 xxxx xxxx			*/
	{ONI_EOM_xx,	3,20,11,L0|L1}, /* cb: 0110 0100 1100 1011 xxxx xxxx			*/
	{illegal2,		3,11,11,L0|L1}, /* cc: 0110 0100 1100 1100 xxxx xxxx			*/
	{ONI_TMM_xx,	3,20,11,L0|L1}, /* cd: 0110 0100 1100 1101 xxxx xxxx			*/
	{illegal2,		3,11,11,L0|L1}, /* ce: 0110 0100 1100 1110 xxxx xxxx			*/
	{illegal2,		3,11,11,L0|L1}, /* cf: 0110 0100 1100 1111 xxxx xxxx			*/

	{ACI_ANM_xx,	3,20,11,L0|L1}, /* d0: 0110 0100 1101 0000 xxxx xxxx			*/
	{ACI_SMH_xx,	3,20,11,L0|L1}, /* d1: 0110 0100 1101 0001 xxxx xxxx			*/
	{illegal2,		3,11,11,L0|L1}, /* d2: 0110 0100 1101 0010 xxxx xxxx			*/
	{ACI_EOM_xx,	3,20,11,L0|L1}, /* d3: 0110 0100 1101 0011 xxxx xxxx			*/
	{illegal2,		3,11,11,L0|L1}, /* d4: 0110 0100 1101 0100 xxxx xxxx			*/
	{ACI_TMM_xx,	3,20,11,L0|L1}, /* d5: 0110 0100 1101 0101 xxxx xxxx			*/
	{illegal2,		3,11,11,L0|L1}, /* d6: 0110 0100 1101 0110 xxxx xxxx			*/
	{illegal2,		3,11,11,L0|L1}, /* d7: 0110 0100 1101 0111 xxxx xxxx			*/
	{OFFI_ANM_xx,	3,20,11,L0|L1}, /* d8: 0110 0100 1101 1000 xxxx xxxx			*/
	{OFFI_SMH_xx,	3,20,11,L0|L1}, /* d9: 0110 0100 1101 1001 xxxx xxxx			*/
	{illegal2,		3,11,11,L0|L1}, /* da: 0110 0100 1101 1010 xxxx xxxx			*/
	{OFFI_EOM_xx,	3,20,11,L0|L1}, /* db: 0110 0100 1101 1011 xxxx xxxx			*/
	{illegal2,		3,11,11,L0|L1}, /* dc: 0110 0100 1101 1100 xxxx xxxx			*/
	{OFFI_TMM_xx,	3,20,11,L0|L1}, /* dd: 0110 0100 1101 1101 xxxx xxxx			*/
	{illegal2,		3,11,11,L0|L1}, /* de: 0110 0100 1101 1110 xxxx xxxx			*/
	{illegal2,		3,11,11,L0|L1}, /* df: 0110 0100 1101 1111 xxxx xxxx			*/

	{SUI_ANM_xx,	3,20,11,L0|L1}, /* e0: 0110 0100 1110 0000 xxxx xxxx			*/
	{SUI_SMH_xx,	3,20,11,L0|L1}, /* e1: 0110 0100 1110 0001 xxxx xxxx			*/
	{illegal2,		3,11,11,L0|L1}, /* e2: 0110 0100 1110 0010 xxxx xxxx			*/
	{SUI_EOM_xx,	3,20,11,L0|L1}, /* e3: 0110 0100 1110 0011 xxxx xxxx			*/
	{illegal2,		3,11,11,L0|L1}, /* e4: 0110 0100 1110 0100 xxxx xxxx			*/
	{SUI_TMM_xx,	3,20,11,L0|L1}, /* e5: 0110 0100 1110 0101 xxxx xxxx			*/
	{illegal2,		3,11,11,L0|L1}, /* e6: 0110 0100 1110 0110 xxxx xxxx			*/
	{illegal2,		3,11,11,L0|L1}, /* e7: 0110 0100 1110 0111 xxxx xxxx			*/
	{NEI_ANM_xx,	3,20,11,L0|L1}, /* e8: 0110 0100 1110 1000 xxxx xxxx			*/
	{NEI_SMH_xx,	3,20,11,L0|L1}, /* e9: 0110 0100 1110 1001 xxxx xxxx			*/
	{illegal2,		3,11,11,L0|L1}, /* ea: 0110 0100 1110 1010 xxxx xxxx			*/
	{NEI_EOM_xx,	3,20,11,L0|L1}, /* eb: 0110 0100 1110 1011 xxxx xxxx			*/
	{illegal2,		3,11,11,L0|L1}, /* ec: 0110 0100 1110 1100 xxxx xxxx			*/
	{NEI_TMM_xx,	3,20,11,L0|L1}, /* ed: 0110 0100 1110 1101 xxxx xxxx			*/
	{illegal2,		3,11,11,L0|L1}, /* ee: 0110 0100 1110 1110 xxxx xxxx			*/
	{illegal2,		3,11,11,L0|L1}, /* ef: 0110 0100 1110 1111 xxxx xxxx			*/

	{SBI_ANM_xx,	3,20,11,L0|L1}, /* f0: 0110 0100 1111 0000 xxxx xxxx			*/
	{SBI_SMH_xx,	3,20,11,L0|L1}, /* f1: 0110 0100 1111 0001 xxxx xxxx			*/
	{illegal2,		3,11,11,L0|L1}, /* f2: 0110 0100 1111 0010 xxxx xxxx			*/
	{SBI_EOM_xx,	3,20,11,L0|L1}, /* f3: 0110 0100 1111 0011 xxxx xxxx			*/
	{illegal2,		3,11,11,L0|L1}, /* f4: 0110 0100 1111 0100 xxxx xxxx			*/
	{SBI_TMM_xx,	3,20,11,L0|L1}, /* f5: 0110 0100 1111 0101 xxxx xxxx			*/
	{illegal2,		3,11,11,L0|L1}, /* f6: 0110 0100 1111 0110 xxxx xxxx			*/
	{illegal2,		3,11,11,L0|L1}, /* f7: 0110 0100 1111 0111 xxxx xxxx			*/
	{EQI_ANM_xx,	3,20,11,L0|L1}, /* f8: 0110 0100 1111 1000 xxxx xxxx			*/
	{EQI_SMH_xx,	3,20,11,L0|L1}, /* f9: 0110 0100 1111 1001 xxxx xxxx			*/
	{illegal2,		3,11,11,L0|L1}, /* fa: 0110 0100 1111 1010 xxxx xxxx			*/
	{EQI_EOM_xx,	3,20,11,L0|L1}, /* fb: 0110 0100 1111 1011 xxxx xxxx			*/
	{illegal2,		3,11,11,L0|L1}, /* fc: 0110 0100 1111 1100 xxxx xxxx			*/
	{EQI_TMM_xx,	3,20,11,L0|L1}, /* fd: 0110 0100 1111 1101 xxxx xxxx			*/
	{illegal2,		3,11,11,L0|L1}, /* fe: 0110 0100 1111 1110 xxxx xxxx			*/
	{illegal2,		3,11,11,L0|L1}	/* ff: 0110 0100 1111 1111 xxxx xxxx			*/
};

/* prefix 70 */
static struct opcode_s op70[256] =
{
	{illegal2,		2, 8, 8,L0|L1}, /* 00: 0111 0000 0000 0000						*/
	{illegal2,		2, 8, 8,L0|L1}, /* 01: 0111 0000 0000 0001						*/
	{illegal2,		2, 8, 8,L0|L1}, /* 02: 0111 0000 0000 0010						*/
	{illegal2,		2, 8, 8,L0|L1}, /* 03: 0111 0000 0000 0011						*/
	{illegal2,		2, 8, 8,L0|L1}, /* 04: 0111 0000 0000 0100						*/
	{illegal2,		2, 8, 8,L0|L1}, /* 05: 0111 0000 0000 0101						*/
	{illegal2,		2, 8, 8,L0|L1}, /* 06: 0111 0000 0000 0110						*/
	{illegal2,		2, 8, 8,L0|L1}, /* 07: 0111 0000 0000 0111						*/
	{illegal2,		2, 8, 8,L0|L1}, /* 08: 0111 0000 0000 1000						*/
	{illegal2,		2, 8, 8,L0|L1}, /* 09: 0111 0000 0000 1001						*/
	{illegal2,		2, 8, 8,L0|L1}, /* 0a: 0111 0000 0000 1010						*/
	{illegal2,		2, 8, 8,L0|L1}, /* 0b: 0111 0000 0000 1011						*/
	{illegal2,		2, 8, 8,L0|L1}, /* 0c: 0111 0000 0000 1100						*/
	{illegal2,		2, 8, 8,L0|L1}, /* 0d: 0111 0000 0000 1101						*/
	{SSPD_w,		4,20,20,L0|L1}, /* 0e: 0111 0000 0000 1110 llll llll hhhh hhhh	*/
	{LSPD_w,		4,20,20,L0|L1}, /* 0f: 0111 0000 0000 1111 llll llll hhhh hhhh	*/

	{illegal2,		2, 8, 8,L0|L1}, /* 10: 0111 0000 0001 0000						*/
	{illegal2,		2, 8, 8,L0|L1}, /* 11: 0111 0000 0001 0001						*/
	{illegal2,		2, 8, 8,L0|L1}, /* 12: 0111 0000 0001 0010						*/
	{illegal2,		2, 8, 8,L0|L1}, /* 13: 0111 0000 0001 0011						*/
	{illegal2,		2, 8, 8,L0|L1}, /* 14: 0111 0000 0001 0100						*/
	{illegal2,		2, 8, 8,L0|L1}, /* 15: 0111 0000 0001 0101						*/
	{illegal2,		2, 8, 8,L0|L1}, /* 16: 0111 0000 0001 0110						*/
	{illegal2,		2, 8, 8,L0|L1}, /* 17: 0111 0000 0001 0111						*/
	{illegal2,		2, 8, 8,L0|L1}, /* 18: 0111 0000 0001 1000						*/
	{illegal2,		2, 8, 8,L0|L1}, /* 19: 0111 0000 0001 1001						*/
	{illegal2,		2, 8, 8,L0|L1}, /* 1a: 0111 0000 0001 1010						*/
	{illegal2,		2, 8, 8,L0|L1}, /* 1b: 0111 0000 0001 1011						*/
	{illegal2,		2, 8, 8,L0|L1}, /* 1c: 0111 0000 0001 1100						*/
	{illegal2,		2, 8, 8,L0|L1}, /* 1d: 0111 0000 0001 1101						*/
	{SBCD_w,		4,20,20,L0|L1}, /* 1e: 0111 0000 0001 1110 llll llll hhhh hhhh	*/
	{LBCD_w,		4,20,20,L0|L1}, /* 1f: 0111 0000 0001 1111 llll llll hhhh hhhh	*/

	{illegal2,		2, 8, 8,L0|L1}, /* 20: 0111 0000 0010 0000						*/
	{illegal2,		2, 8, 8,L0|L1}, /* 21: 0111 0000 0010 0001						*/
	{illegal2,		2, 8, 8,L0|L1}, /* 22: 0111 0000 0010 0010						*/
	{illegal2,		2, 8, 8,L0|L1}, /* 23: 0111 0000 0010 0011						*/
	{illegal2,		2, 8, 8,L0|L1}, /* 24: 0111 0000 0010 0100						*/
	{illegal2,		2, 8, 8,L0|L1}, /* 25: 0111 0000 0010 0101						*/
	{illegal2,		2, 8, 8,L0|L1}, /* 26: 0111 0000 0010 0110						*/
	{illegal2,		2, 8, 8,L0|L1}, /* 27: 0111 0000 0010 0111						*/
	{illegal2,		2, 8, 8,L0|L1}, /* 28: 0111 0000 0010 1000						*/
	{illegal2,		2, 8, 8,L0|L1}, /* 29: 0111 0000 0010 1001						*/
	{illegal2,		2, 8, 8,L0|L1}, /* 2a: 0111 0000 0010 1010						*/
	{illegal2,		2, 8, 8,L0|L1}, /* 2b: 0111 0000 0010 1011						*/
	{illegal2,		2, 8, 8,L0|L1}, /* 2c: 0111 0000 0010 1100						*/
	{illegal2,		2, 8, 8,L0|L1}, /* 2d: 0111 0000 0010 1101						*/
	{SDED_w,		4,20,20,L0|L1}, /* 2e: 0111 0000 0010 1110 llll llll hhhh hhhh	*/
	{LDED_w,		4,20,20,L0|L1}, /* 2f: 0111 0000 0010 1111 llll llll hhhh hhhh	*/

	{illegal2,		2, 8, 8,L0|L1}, /* 30: 0111 0000 0011 0000						*/
	{illegal2,		2, 8, 8,L0|L1}, /* 31: 0111 0000 0011 0001						*/
	{illegal2,		2, 8, 8,L0|L1}, /* 32: 0111 0000 0011 0010						*/
	{illegal2,		2, 8, 8,L0|L1}, /* 33: 0111 0000 0011 0011						*/
	{illegal2,		2, 8, 8,L0|L1}, /* 34: 0111 0000 0011 0100						*/
	{illegal2,		2, 8, 8,L0|L1}, /* 35: 0111 0000 0011 0101						*/
	{illegal2,		2, 8, 8,L0|L1}, /* 36: 0111 0000 0011 0110						*/
	{illegal2,		2, 8, 8,L0|L1}, /* 37: 0111 0000 0011 0111						*/
	{illegal2,		2, 8, 8,L0|L1}, /* 38: 0111 0000 0011 1000						*/
	{illegal2,		2, 8, 8,L0|L1}, /* 39: 0111 0000 0011 1001						*/
	{illegal2,		2, 8, 8,L0|L1}, /* 3a: 0111 0000 0011 1010						*/
	{illegal2,		2, 8, 8,L0|L1}, /* 3b: 0111 0000 0011 1011						*/
	{illegal2,		2, 8, 8,L0|L1}, /* 3c: 0111 0000 0011 1100						*/
	{illegal2,		2, 8, 8,L0|L1}, /* 3d: 0111 0000 0011 1101						*/
	{SHLD_w,		4,20,20,L0|L1}, /* 3e: 0111 0000 0011 1110 llll llll hhhh hhhh	*/
	{LHLD_w,		4,20,20,L0|L1}, /* 3f: 0111 0000 0011 1111 llll llll hhhh hhhh	*/

	{illegal2,		2, 8, 8,L0|L1}, /* 40: 0111 0000 0100 0000						*/
	{EADD_EA_A, 	2,11,11,L0|L1}, /* 41: 0111 0000 0100 0001						*/
	{EADD_EA_B, 	2,11,11,L0|L1}, /* 42: 0111 0000 0100 0010						*/
	{EADD_EA_C, 	2,11,11,L0|L1}, /* 43: 0111 0000 0100 0011						*/
	{illegal2,		2, 8, 8,L0|L1}, /* 44: 0111 0000 0100 0100						*/
	{illegal2,		2, 8, 8,L0|L1}, /* 45: 0111 0000 0100 0101						*/
	{illegal2,		2, 8, 8,L0|L1}, /* 46: 0111 0000 0100 0110						*/
	{illegal2,		2, 8, 8,L0|L1}, /* 47: 0111 0000 0100 0111						*/
	{illegal2,		2, 8, 8,L0|L1}, /* 48: 0111 0000 0100 1000						*/
	{illegal2,		2, 8, 8,L0|L1}, /* 49: 0111 0000 0100 1001						*/
	{illegal2,		2, 8, 8,L0|L1}, /* 4a: 0111 0000 0100 1010						*/
	{illegal2,		2, 8, 8,L0|L1}, /* 4b: 0111 0000 0100 1011						*/
	{illegal2,		2, 8, 8,L0|L1}, /* 4c: 0111 0000 0100 1100						*/
	{illegal2,		2, 8, 8,L0|L1}, /* 4d: 0111 0000 0100 1101						*/
	{illegal2,		2, 8, 8,L0|L1}, /* 4e: 0111 0000 0100 1110						*/
	{illegal2,		2, 8, 8,L0|L1}, /* 4f: 0111 0000 0100 1111						*/

	{illegal2,		2, 8, 8,L0|L1}, /* 50: 0111 0000 0101 0000						*/
	{illegal2,		2, 8, 8,L0|L1}, /* 51: 0111 0000 0101 0001						*/
	{illegal2,		2, 8, 8,L0|L1}, /* 52: 0111 0000 0101 0010						*/
	{illegal2,		2, 8, 8,L0|L1}, /* 53: 0111 0000 0101 0011						*/
	{illegal2,		2, 8, 8,L0|L1}, /* 54: 0111 0000 0101 0100						*/
	{illegal2,		2, 8, 8,L0|L1}, /* 55: 0111 0000 0101 0101						*/
	{illegal2,		2, 8, 8,L0|L1}, /* 56: 0111 0000 0101 0110						*/
	{illegal2,		2, 8, 8,L0|L1}, /* 57: 0111 0000 0101 0111						*/
	{illegal2,		2, 8, 8,L0|L1}, /* 58: 0111 0000 0101 1000						*/
	{illegal2,		2, 8, 8,L0|L1}, /* 59: 0111 0000 0101 1001						*/
	{illegal2,		2, 8, 8,L0|L1}, /* 5a: 0111 0000 0101 1010						*/
	{illegal2,		2, 8, 8,L0|L1}, /* 5b: 0111 0000 0101 1011						*/
	{illegal2,		2, 8, 8,L0|L1}, /* 5c: 0111 0000 0101 1100						*/
	{illegal2,		2, 8, 8,L0|L1}, /* 5d: 0111 0000 0101 1101						*/
	{illegal2,		2, 8, 8,L0|L1}, /* 5e: 0111 0000 0101 1110						*/
	{illegal2,		2, 8, 8,L0|L1}, /* 5f: 0111 0000 0101 1111						*/

	{illegal2,		2, 8, 8,L0|L1}, /* 60: 0111 0000 0110 0000						*/
	{ESUB_EA_A, 	2,11,11,L0|L1}, /* 61: 0111 0000 0110 0001						*/
	{ESUB_EA_B, 	2,11,11,L0|L1}, /* 62: 0111 0000 0110 0010						*/
	{ESUB_EA_C, 	2,11,11,L0|L1}, /* 63: 0111 0000 0110 0011						*/
	{illegal2,		2, 8, 8,L0|L1}, /* 64: 0111 0000 0110 0100						*/
	{illegal2,		2, 8, 8,L0|L1}, /* 65: 0111 0000 0110 0101						*/
	{illegal2,		2, 8, 8,L0|L1}, /* 66: 0111 0000 0110 0110						*/
	{illegal2,		2, 8, 8,L0|L1}, /* 67: 0111 0000 0110 0111						*/
	{MOV_V_w,		4,17,17,L0|L1}, /* 68: 0111 0000 0110 1000 llll llll hhhh hhhh	*/
	{MOV_A_w,		4,17,17,L0|L1}, /* 69: 0111 0000 0110 1001 llll llll hhhh hhhh	*/
	{MOV_B_w,		4,17,17,L0|L1}, /* 6a: 0111 0000 0110 1010 llll llll hhhh hhhh	*/
	{MOV_C_w,		4,17,17,L0|L1}, /* 6b: 0111 0000 0110 1011 llll llll hhhh hhhh	*/
	{MOV_D_w,		4,17,17,L0|L1}, /* 6c: 0111 0000 0110 1100 llll llll hhhh hhhh	*/
	{MOV_E_w,		4,17,17,L0|L1}, /* 6d: 0111 0000 0110 1101 llll llll hhhh hhhh	*/
	{MOV_H_w,		4,17,17,L0|L1}, /* 6e: 0111 0000 0110 1110 llll llll hhhh hhhh	*/
	{MOV_L_w,		4,17,17,L0|L1}, /* 6f: 0111 0000 0110 1111 llll llll hhhh hhhh	*/

	{illegal2,		2, 8, 8,L0|L1}, /* 70: 0111 0000 0111 0000						*/
	{illegal2,		2, 8, 8,L0|L1}, /* 71: 0111 0000 0111 0001						*/
	{illegal2,		2, 8, 8,L0|L1}, /* 72: 0111 0000 0111 0010						*/
	{illegal2,		2, 8, 8,L0|L1}, /* 73: 0111 0000 0111 0011						*/
	{illegal2,		2, 8, 8,L0|L1}, /* 74: 0111 0000 0111 0100						*/
	{illegal2,		2, 8, 8,L0|L1}, /* 75: 0111 0000 0111 0101						*/
	{illegal2,		2, 8, 8,L0|L1}, /* 76: 0111 0000 0111 0110						*/
	{illegal2,		2, 8, 8,L0|L1}, /* 77: 0111 0000 0111 0111						*/
	{MOV_w_V,		4,17,17,L0|L1}, /* 78: 0111 0000 0111 1000 llll llll hhhh hhhh	*/
	{MOV_w_A,		4,17,17,L0|L1}, /* 79: 0111 0000 0111 1001 llll llll hhhh hhhh	*/
	{MOV_w_B,		4,17,17,L0|L1}, /* 7a: 0111 0000 0111 1010 llll llll hhhh hhhh	*/
	{MOV_w_C,		4,17,17,L0|L1}, /* 7b: 0111 0000 0111 1011 llll llll hhhh hhhh	*/
	{MOV_w_D,		4,17,17,L0|L1}, /* 7c: 0111 0000 0111 1100 llll llll hhhh hhhh	*/
	{MOV_w_E,		4,17,17,L0|L1}, /* 7d: 0111 0000 0111 1101 llll llll hhhh hhhh	*/
	{MOV_w_H,		4,17,17,L0|L1}, /* 7e: 0111 0000 0111 1110 llll llll hhhh hhhh	*/
	{MOV_w_L,		4,17,17,L0|L1}, /* 7f: 0111 0000 0111 1111 llll llll hhhh hhhh	*/

	{illegal2,		2, 8, 8,L0|L1}, /* 80: 0111 0000 1000 0000						*/
	{illegal2,		2, 8, 8,L0|L1}, /* 81: 0111 0000 1000 0001						*/
	{illegal2,		2, 8, 8,L0|L1}, /* 82: 0111 0000 1000 0010						*/
	{illegal2,		2, 8, 8,L0|L1}, /* 83: 0111 0000 1000 0011						*/
	{illegal2,		2, 8, 8,L0|L1}, /* 84: 0111 0000 1000 0100						*/
	{illegal2,		2, 8, 8,L0|L1}, /* 85: 0111 0000 1000 0101						*/
	{illegal2,		2, 8, 8,L0|L1}, /* 86: 0111 0000 1000 0110						*/
	{illegal2,		2, 8, 8,L0|L1}, /* 87: 0111 0000 1000 0111						*/
	{illegal2,		2, 8, 8,L0|L1}, /* 88: 0111 0000 1000 1000						*/
	{ANAX_B,		2,11,11,L0|L1}, /* 89: 0111 0000 1000 1001						*/
	{ANAX_D,		2,11,11,L0|L1}, /* 8a: 0111 0000 1000 1010						*/
	{ANAX_H,		2,11,11,L0|L1}, /* 8b: 0111 0000 1000 1011						*/
	{ANAX_Dp,		2,11,11,L0|L1}, /* 8c: 0111 0000 1000 1100						*/
	{ANAX_Hp,		2,11,11,L0|L1}, /* 8d: 0111 0000 1000 1101						*/
	{ANAX_Dm,		2,11,11,L0|L1}, /* 8e: 0111 0000 1000 1110						*/
	{ANAX_Hm,		2,11,11,L0|L1}, /* 8f: 0111 0000 1000 1111						*/

	{illegal2,		2, 8, 8,L0|L1}, /* 90: 0111 0000 1001 0000						*/
	{XRAX_B,		2,11,11,L0|L1}, /* 91: 0111 0000 1001 0001						*/
	{XRAX_D,		2,11,11,L0|L1}, /* 92: 0111 0000 1001 0010						*/
	{XRAX_H,		2,11,11,L0|L1}, /* 93: 0111 0000 1001 0011						*/
	{XRAX_Dp,		2,11,11,L0|L1}, /* 94: 0111 0000 1001 0100						*/
	{XRAX_Hp,		2,11,11,L0|L1}, /* 95: 0111 0000 1001 0101						*/
	{XRAX_Dm,		2,11,11,L0|L1}, /* 96: 0111 0000 1001 0110						*/
	{XRAX_Hm,		2,11,11,L0|L1}, /* 97: 0111 0000 1001 0111						*/
	{illegal2,		2, 8, 8,L0|L1}, /* 98: 0111 0000 1001 1000						*/
// orax added, timings not verified
	{ORAX_B,		2,11, 8,L0|L1}, /* 99: 0111 0000 1001 1001						*/
	{ORAX_D,		2,11, 8,L0|L1}, /* 9a: 0111 0000 1001 1010						*/
	{ORAX_H,		2,11, 8,L0|L1}, /* 9b: 0111 0000 1001 1011						*/
	{ORAX_Dp,		2,11, 8,L0|L1}, /* 9c: 0111 0000 1001 1100						*/
	{ORAX_Hp,		2,11, 8,L0|L1}, /* 9d: 0111 0000 1001 1101						*/
	{ORAX_Dm,		2,11, 8,L0|L1}, /* 9e: 0111 0000 1001 1110						*/
	{ORAX_Hm,		2,11, 8,L0|L1}, /* 9f: 0111 0000 1001 1111						*/

	{illegal2,		2, 8, 8,L0|L1}, /* a0: 0111 0000 1010 0000						*/
	{ADDNCX_B,		2,11, 8,L0|L1}, /* a1: 0111 0000 1010 0001						*/
	{ADDNCX_D,		2,11, 8,L0|L1}, /* a2: 0111 0000 1010 0010						*/
	{ADDNCX_H,		2,11, 8,L0|L1}, /* a3: 0111 0000 1010 0011						*/
	{ADDNCX_Dp, 	2,11, 8,L0|L1}, /* a4: 0111 0000 1010 0100						*/
	{ADDNCX_Hp, 	2,11, 8,L0|L1}, /* a5: 0111 0000 1010 0101						*/
	{ADDNCX_Dm, 	2,11, 8,L0|L1}, /* a6: 0111 0000 1010 0110						*/
	{ADDNCX_Hm, 	2,11, 8,L0|L1}, /* a7: 0111 0000 1010 0111						*/
	{illegal2,		2, 8, 8,L0|L1}, /* a8: 0111 0000 1010 1000						*/
	{GTAX_B,		2,11,11,L0|L1}, /* a9: 0111 0000 1010 1001						*/
	{GTAX_D,		2,11,11,L0|L1}, /* aa: 0111 0000 1010 1010						*/
	{GTAX_H,		2,11,11,L0|L1}, /* ab: 0111 0000 1010 1011						*/
	{GTAX_Dp,		2,11,11,L0|L1}, /* ac: 0111 0000 1010 1100						*/
	{GTAX_Hp,		2,11,11,L0|L1}, /* ad: 0111 0000 1010 1101						*/
	{GTAX_Dm,		2,11,11,L0|L1}, /* ae: 0111 0000 1010 1110						*/
	{GTAX_Hm,		2,11,11,L0|L1}, /* af: 0111 0000 1010 1111						*/

	{illegal2,		2, 8, 8,L0|L1}, /* b0: 0111 0000 1011 0000						*/
	{SUBNBX_B,		2,11,11,L0|L1}, /* b1: 0111 0000 1011 0001						*/
	{SUBNBX_D,		2,11,11,L0|L1}, /* b2: 0111 0000 1011 0010						*/
	{SUBNBX_H,		2,11,11,L0|L1}, /* b3: 0111 0000 1011 0011						*/
	{SUBNBX_Dp, 	2,11,11,L0|L1}, /* b4: 0111 0000 1011 0100						*/
	{SUBNBX_Hp, 	2,11,11,L0|L1}, /* b5: 0111 0000 1011 0101						*/
	{SUBNBX_Dm, 	2,11,11,L0|L1}, /* b6: 0111 0000 1011 0110						*/
	{SUBNBX_Hm, 	2,11,11,L0|L1}, /* b7: 0111 0000 1011 0111						*/
	{illegal2,		2, 8, 8,L0|L1}, /* b8: 0111 0000 1011 1000						*/
	{LTAX_B,		2,11,11,L0|L1}, /* b9: 0111 0000 1011 1001						*/
	{LTAX_D,		2,11,11,L0|L1}, /* ba: 0111 0000 1011 1010						*/
	{LTAX_H,		2,11,11,L0|L1}, /* bb: 0111 0000 1011 1011						*/
	{LTAX_Dp,		2,11,11,L0|L1}, /* bc: 0111 0000 1011 1100						*/
	{LTAX_Hp,		2,11,11,L0|L1}, /* bd: 0111 0000 1011 1101						*/
	{LTAX_Dm,		2,11,11,L0|L1}, /* be: 0111 0000 1011 1110						*/
	{LTAX_Hm,		2,11,11,L0|L1}, /* bf: 0111 0000 1011 1111						*/

	{illegal2,		2, 8, 8,L0|L1}, /* c0: 0111 0000 1100 0000						*/
	{ADDX_B,		2,11, 8,L0|L1}, /* c1: 0111 0000 1100 0001						*/
	{ADDX_D,		2,11, 8,L0|L1}, /* c2: 0111 0000 1100 0010						*/
	{ADDX_H,		2,11, 8,L0|L1}, /* c3: 0111 0000 1100 0011						*/
	{ADDX_Dp,		2,11, 8,L0|L1}, /* c4: 0111 0000 1100 0100						*/
	{ADDX_Hp,		2,11, 8,L0|L1}, /* c5: 0111 0000 1100 0101						*/
	{ADDX_Dm,		2,11, 8,L0|L1}, /* c6: 0111 0000 1100 0110						*/
	{ADDX_Hm,		2,11, 8,L0|L1}, /* c7: 0111 0000 1100 0111						*/
	{illegal2,		2, 8, 8,L0|L1}, /* c8: 0111 0000 1100 1000						*/
	{ONAX_B,		2,11, 8,L0|L1}, /* c9: 0111 0000 1100 1001						*/
	{ONAX_D,		2,11, 8,L0|L1}, /* ca: 0111 0000 1100 1010						*/
	{ONAX_H,		2,11, 8,L0|L1}, /* cb: 0111 0000 1100 1011						*/
	{ONAX_Dp,		2,11, 8,L0|L1}, /* cc: 0111 0000 1100 1100						*/
	{ONAX_Hp,		2,11, 8,L0|L1}, /* cd: 0111 0000 1100 1101						*/
	{ONAX_Dm,		2,11, 8,L0|L1}, /* ce: 0111 0000 1100 1110						*/
	{ONAX_Hm,		2,11, 8,L0|L1}, /* cf: 0111 0000 1100 1111						*/

	{illegal2,		2, 8, 8,L0|L1}, /* d0: 0111 0000 1101 0000						*/
	{ADCX_B,		2,11, 8,L0|L1}, /* d1: 0111 0000 1101 0001						*/
	{ADCX_D,		2,11, 8,L0|L1}, /* d2: 0111 0000 1101 0010						*/
	{ADCX_H,		2,11, 8,L0|L1}, /* d3: 0111 0000 1101 0011						*/
	{ADCX_Dp,		2,11, 8,L0|L1}, /* d4: 0111 0000 1101 0100						*/
	{ADCX_Hp,		2,11, 8,L0|L1}, /* d5: 0111 0000 1101 0101						*/
	{ADCX_Dm,		2,11, 8,L0|L1}, /* d6: 0111 0000 1101 0110						*/
	{ADCX_Hm,		2,11, 8,L0|L1}, /* d7: 0111 0000 1101 0111						*/
	{illegal2,		2, 8, 8,L0|L1}, /* d8: 0111 0000 1101 1000						*/
	{OFFAX_B,		2,11, 8,L0|L1}, /* d9: 0111 0000 1101 1001						*/
	{OFFAX_D,		2,11, 8,L0|L1}, /* da: 0111 0000 1101 1010						*/
	{OFFAX_H,		2,11, 8,L0|L1}, /* db: 0111 0000 1101 1011						*/
	{OFFAX_Dp,		2,11, 8,L0|L1}, /* dc: 0111 0000 1101 1100						*/
	{OFFAX_Hp,		2,11, 8,L0|L1}, /* dd: 0111 0000 1101 1101						*/
	{OFFAX_Dm,		2,11, 8,L0|L1}, /* de: 0111 0000 1101 1110						*/
	{OFFAX_Hm,		2,11, 8,L0|L1}, /* df: 0111 0000 1101 1111						*/

	{illegal2,		2, 8, 8,L0|L1}, /* e0: 0111 0000 1110 0000						*/
	{SUBX_B,		2,11,11,L0|L1}, /* e1: 0111 0000 1110 0001						*/
	{SUBX_D,		2,11,11,L0|L1}, /* e2: 0111 0000 1110 0010						*/
	{SUBX_H,		2,11,11,L0|L1}, /* e3: 0111 0000 1110 0011						*/
	{SUBX_Dp,		2,11,11,L0|L1}, /* e4: 0111 0000 1110 0100						*/
	{SUBX_Hp,		2,11,11,L0|L1}, /* e5: 0111 0000 1110 0101						*/
	{SUBX_Dm,		2,11,11,L0|L1}, /* e6: 0111 0000 1110 0110						*/
	{SUBX_Hm,		2,11,11,L0|L1}, /* e7: 0111 0000 1110 0111						*/
	{illegal2,		2, 8, 8,L0|L1}, /* e8: 0111 0000 1110 1000						*/
	{NEAX_B,		2,11,11,L0|L1}, /* e9: 0111 0000 1110 1001						*/
	{NEAX_D,		2,11,11,L0|L1}, /* ea: 0111 0000 1110 1010						*/
	{NEAX_H,		2,11,11,L0|L1}, /* eb: 0111 0000 1110 1011						*/
	{NEAX_Dp,		2,11,11,L0|L1}, /* ec: 0111 0000 1110 1100						*/
	{NEAX_Hp,		2,11,11,L0|L1}, /* ed: 0111 0000 1110 1101						*/
	{NEAX_Dm,		2,11,11,L0|L1}, /* ee: 0111 0000 1110 1110						*/
	{NEAX_Hm,		2,11,11,L0|L1}, /* ef: 0111 0000 1110 1111						*/

	{illegal2,		2, 8, 8,L0|L1}, /* f0: 0111 0000 1111 0000						*/
	{SBBX_B,		2,11,11,L0|L1}, /* f1: 0111 0000 1111 0001						*/
	{SBBX_D,		2,11,11,L0|L1}, /* f2: 0111 0000 1111 0010						*/
	{SBBX_H,		2,11,11,L0|L1}, /* f3: 0111 0000 1111 0011						*/
	{SBBX_Dp,		2,11,11,L0|L1}, /* f4: 0111 0000 1111 0100						*/
	{SBBX_Hp,		2,11,11,L0|L1}, /* f5: 0111 0000 1111 0101						*/
	{SBBX_Dm,		2,11,11,L0|L1}, /* f6: 0111 0000 1111 0110						*/
	{SBBX_Hm,		2,11,11,L0|L1}, /* f7: 0111 0000 1111 0111						*/
	{illegal2,		2, 8, 8,L0|L1}, /* f8: 0111 0000 1111 1000						*/
	{EQAX_B,		2,11,11,L0|L1}, /* f9: 0111 0000 1111 1001						*/
	{EQAX_D,		2,11,11,L0|L1}, /* fa: 0111 0000 1111 1010						*/
	{EQAX_H,		2,11,11,L0|L1}, /* fb: 0111 0000 1111 1011						*/
	{EQAX_Dp,		2,11,11,L0|L1}, /* fc: 0111 0000 1111 1100						*/
	{EQAX_Hp,		2,11,11,L0|L1}, /* fd: 0111 0000 1111 1101						*/
	{EQAX_Dm,		2,11,11,L0|L1}, /* fe: 0111 0000 1111 1110						*/
	{EQAX_Hm,		2,11,11,L0|L1}	/* ff: 0111 0000 1111 1111						*/
};

/* prefix 74 */
static struct opcode_s op74[256] =
{
	{illegal2,		2, 8, 8,L0|L1}, /* 00: 0111 0100 0000 0000						*/
	{illegal2,		2, 8, 8,L0|L1}, /* 01: 0111 0100 0000 0001						*/
	{illegal2,		2, 8, 8,L0|L1}, /* 02: 0111 0100 0000 0010						*/
	{illegal2,		2, 8, 8,L0|L1}, /* 03: 0111 0100 0000 0011						*/
	{illegal2,		2, 8, 8,L0|L1}, /* 04: 0111 0100 0000 0100						*/
	{illegal2,		2, 8, 8,L0|L1}, /* 05: 0111 0100 0000 0101						*/
	{illegal2,		2, 8, 8,L0|L1}, /* 06: 0111 0100 0000 0110						*/
	{illegal2,		2, 8, 8,L0|L1}, /* 07: 0111 0100 0000 0111						*/
	{ANI_V_xx,		3,11,11,L0|L1}, /* 08: 0111 0100 0000 1000 xxxx xxxx			*/
	{ANI_A_xx,		3,11,11,L0|L1}, /* 09: 0111 0100 0000 1001 xxxx xxxx			*/
	{ANI_B_xx,		3,11,11,L0|L1}, /* 0a: 0111 0100 0000 1010 xxxx xxxx			*/
	{ANI_C_xx,		3,11,11,L0|L1}, /* 0b: 0111 0100 0000 1011 xxxx xxxx			*/
	{ANI_D_xx,		3,11,11,L0|L1}, /* 0c: 0111 0100 0000 1100 xxxx xxxx			*/
	{ANI_E_xx,		3,11,11,L0|L1}, /* 0d: 0111 0100 0000 1101 xxxx xxxx			*/
	{ANI_H_xx,		3,11,11,L0|L1}, /* 0e: 0111 0100 0000 1110 xxxx xxxx			*/
	{ANI_L_xx,		3,11,11,L0|L1}, /* 0f: 0111 0100 0000 1111 xxxx xxxx			*/

	{XRI_V_xx,		3,11,11,L0|L1}, /* 10: 0111 0100 0001 0000 xxxx xxxx			*/
	{XRI_A_xx,		3,11,11,L0|L1}, /* 11: 0111 0100 0001 0001 xxxx xxxx			*/
	{XRI_B_xx,		3,11,11,L0|L1}, /* 12: 0111 0100 0001 0010 xxxx xxxx			*/
	{XRI_C_xx,		3,11,11,L0|L1}, /* 13: 0111 0100 0001 0011 xxxx xxxx			*/
	{XRI_D_xx,		3,11,11,L0|L1}, /* 14: 0111 0100 0001 0100 xxxx xxxx			*/
	{XRI_E_xx,		3,11,11,L0|L1}, /* 15: 0111 0100 0001 0101 xxxx xxxx			*/
	{XRI_H_xx,		3,11,11,L0|L1}, /* 16: 0111 0100 0001 0110 xxxx xxxx			*/
	{XRI_L_xx,		3,11,11,L0|L1}, /* 17: 0111 0100 0001 0111 xxxx xxxx			*/
	{ORI_V_xx,		3,11,11,L0|L1}, /* 18: 0111 0100 0001 1000 xxxx xxxx			*/
	{ORI_A_xx,		3,11,11,L0|L1}, /* 19: 0111 0100 0001 1001 xxxx xxxx			*/
	{ORI_B_xx,		3,11,11,L0|L1}, /* 1a: 0111 0100 0001 1010 xxxx xxxx			*/
	{ORI_C_xx,		3,11,11,L0|L1}, /* 1b: 0111 0100 0001 1011 xxxx xxxx			*/
	{ORI_D_xx,		3,11,11,L0|L1}, /* 1c: 0111 0100 0001 1100 xxxx xxxx			*/
	{ORI_E_xx,		3,11,11,L0|L1}, /* 1d: 0111 0100 0001 1101 xxxx xxxx			*/
	{ORI_H_xx,		3,11,11,L0|L1}, /* 1e: 0111 0100 0001 1110 xxxx xxxx			*/
	{ORI_L_xx,		3,11,11,L0|L1}, /* 1f: 0111 0100 0001 1111 xxxx xxxx			*/

	{ADINC_V_xx,	3,11,11,L0|L1}, /* 20: 0111 0100 0010 0000 xxxx xxxx			*/
	{ADINC_A_xx,	3,11,11,L0|L1}, /* 21: 0111 0100 0010 0001 xxxx xxxx			*/
	{ADINC_B_xx,	3,11,11,L0|L1}, /* 22: 0111 0100 0010 0010 xxxx xxxx			*/
	{ADINC_C_xx,	3,11,11,L0|L1}, /* 23: 0111 0100 0010 0011 xxxx xxxx			*/
	{ADINC_D_xx,	3,11,11,L0|L1}, /* 24: 0111 0100 0010 0100 xxxx xxxx			*/
	{ADINC_E_xx,	3,11,11,L0|L1}, /* 25: 0111 0100 0010 0101 xxxx xxxx			*/
	{ADINC_H_xx,	3,11,11,L0|L1}, /* 26: 0111 0100 0010 0110 xxxx xxxx			*/
	{ADINC_L_xx,	3,11,11,L0|L1}, /* 27: 0111 0100 0010 0111 xxxx xxxx			*/
	{GTI_V_xx,		3,11,11,L0|L1}, /* 28: 0111 0100 0010 1000 xxxx xxxx			*/
	{GTI_A_xx,		3,11,11,L0|L1}, /* 29: 0111 0100 0010 1001 xxxx xxxx			*/
	{GTI_B_xx,		3,11,11,L0|L1}, /* 2a: 0111 0100 0010 1010 xxxx xxxx			*/
	{GTI_C_xx,		3,11,11,L0|L1}, /* 2b: 0111 0100 0010 1011 xxxx xxxx			*/
	{GTI_D_xx,		3,11,11,L0|L1}, /* 2c: 0111 0100 0010 1100 xxxx xxxx			*/
	{GTI_E_xx,		3,11,11,L0|L1}, /* 2d: 0111 0100 0010 1101 xxxx xxxx			*/
	{GTI_H_xx,		3,11,11,L0|L1}, /* 2e: 0111 0100 0010 1110 xxxx xxxx			*/
	{GTI_L_xx,		3,11,11,L0|L1}, /* 2f: 0111 0100 0010 1111 xxxx xxxx			*/

	{SUINB_V_xx,	3,11,11,L0|L1}, /* 30: 0111 0100 0011 0000 xxxx xxxx			*/
	{SUINB_A_xx,	3,11,11,L0|L1}, /* 31: 0111 0100 0011 0001 xxxx xxxx			*/
	{SUINB_B_xx,	3,11,11,L0|L1}, /* 32: 0111 0100 0011 0010 xxxx xxxx			*/
	{SUINB_C_xx,	3,11,11,L0|L1}, /* 33: 0111 0100 0011 0011 xxxx xxxx			*/
	{SUINB_D_xx,	3,11,11,L0|L1}, /* 34: 0111 0100 0011 0100 xxxx xxxx			*/
	{SUINB_E_xx,	3,11,11,L0|L1}, /* 35: 0111 0100 0011 0101 xxxx xxxx			*/
	{SUINB_H_xx,	3,11,11,L0|L1}, /* 36: 0111 0100 0011 0110 xxxx xxxx			*/
	{SUINB_L_xx,	3,11,11,L0|L1}, /* 37: 0111 0100 0011 0111 xxxx xxxx			*/
	{LTI_V_xx,		3,11,11,L0|L1}, /* 38: 0111 0100 0011 1000 xxxx xxxx			*/
	{LTI_A_xx,		3,11,11,L0|L1}, /* 39: 0111 0100 0011 1001 xxxx xxxx			*/
	{LTI_B_xx,		3,11,11,L0|L1}, /* 3a: 0111 0100 0011 1010 xxxx xxxx			*/
	{LTI_C_xx,		3,11,11,L0|L1}, /* 3b: 0111 0100 0011 1011 xxxx xxxx			*/
	{LTI_D_xx,		3,11,11,L0|L1}, /* 3c: 0111 0100 0011 1100 xxxx xxxx			*/
	{LTI_E_xx,		3,11,11,L0|L1}, /* 3d: 0111 0100 0011 1101 xxxx xxxx			*/
	{LTI_H_xx,		3,11,11,L0|L1}, /* 3e: 0111 0100 0011 1110 xxxx xxxx			*/
	{LTI_L_xx,		3,11,11,L0|L1}, /* 3f: 0111 0100 0011 1111 xxxx xxxx			*/

	{ADI_V_xx,		3,11,11,L0|L1}, /* 40: 0111 0100 0100 0000 xxxx xxxx			*/
	{ADI_A_xx,		3,11,11,L0|L1}, /* 41: 0111 0100 0100 0001 xxxx xxxx			*/
	{ADI_B_xx,		3,11,11,L0|L1}, /* 42: 0111 0100 0100 0010 xxxx xxxx			*/
	{ADI_C_xx,		3,11,11,L0|L1}, /* 43: 0111 0100 0100 0011 xxxx xxxx			*/
	{ADI_D_xx,		3,11,11,L0|L1}, /* 44: 0111 0100 0100 0100 xxxx xxxx			*/
	{ADI_E_xx,		3,11,11,L0|L1}, /* 45: 0111 0100 0100 0101 xxxx xxxx			*/
	{ADI_H_xx,		3,11,11,L0|L1}, /* 46: 0111 0100 0100 0110 xxxx xxxx			*/
	{ADI_L_xx,		3,11,11,L0|L1}, /* 47: 0111 0100 0100 0111 xxxx xxxx			*/
	{ONI_V_xx,		3,11,11,L0|L1}, /* 48: 0111 0100 0100 1000 xxxx xxxx			*/
	{ONI_A_xx,		3,11,11,L0|L1}, /* 49: 0111 0100 0100 1001 xxxx xxxx			*/
	{ONI_B_xx,		3,11,11,L0|L1}, /* 4a: 0111 0100 0100 1010 xxxx xxxx			*/
	{ONI_C_xx,		3,11,11,L0|L1}, /* 4b: 0111 0100 0100 1011 xxxx xxxx			*/
	{ONI_D_xx,		3,11,11,L0|L1}, /* 4c: 0111 0100 0100 1100 xxxx xxxx			*/
	{ONI_E_xx,		3,11,11,L0|L1}, /* 4d: 0111 0100 0100 1101 xxxx xxxx			*/
	{ONI_H_xx,		3,11,11,L0|L1}, /* 4e: 0111 0100 0100 1110 xxxx xxxx			*/
	{ONI_L_xx,		3,11,11,L0|L1}, /* 4f: 0111 0100 0100 1111 xxxx xxxx			*/

	{ACI_V_xx,		3,11,11,L0|L1}, /* 50: 0111 0100 0101 0000 xxxx xxxx			*/
	{ACI_A_xx,		3,11,11,L0|L1}, /* 51: 0111 0100 0101 0001 xxxx xxxx			*/
	{ACI_B_xx,		3,11,11,L0|L1}, /* 52: 0111 0100 0101 0010 xxxx xxxx			*/
	{ACI_C_xx,		3,11,11,L0|L1}, /* 53: 0111 0100 0101 0011 xxxx xxxx			*/
	{ACI_D_xx,		3,11,11,L0|L1}, /* 54: 0111 0100 0101 0100 xxxx xxxx			*/
	{ACI_E_xx,		3,11,11,L0|L1}, /* 55: 0111 0100 0101 0101 xxxx xxxx			*/
	{ACI_H_xx,		3,11,11,L0|L1}, /* 56: 0111 0100 0101 0110 xxxx xxxx			*/
	{ACI_L_xx,		3,11,11,L0|L1}, /* 57: 0111 0100 0101 0111 xxxx xxxx			*/
	{OFFI_V_xx, 	3,11,11,L0|L1}, /* 58: 0111 0100 0101 1000 xxxx xxxx			*/
	{OFFI_A_xx, 	3,11,11,L0|L1}, /* 59: 0111 0100 0101 1001 xxxx xxxx			*/
	{OFFI_B_xx, 	3,11,11,L0|L1}, /* 5a: 0111 0100 0101 1010 xxxx xxxx			*/
	{OFFI_C_xx, 	3,11,11,L0|L1}, /* 5b: 0111 0100 0101 1011 xxxx xxxx			*/
	{OFFI_D_xx, 	3,11,11,L0|L1}, /* 5c: 0111 0100 0101 1100 xxxx xxxx			*/
	{OFFI_E_xx, 	3,11,11,L0|L1}, /* 5d: 0111 0100 0101 1101 xxxx xxxx			*/
	{OFFI_H_xx, 	3,11,11,L0|L1}, /* 5e: 0111 0100 0101 1110 xxxx xxxx			*/
	{OFFI_L_xx, 	3,11,11,L0|L1}, /* 5f: 0111 0100 0101 1111 xxxx xxxx			*/

	{SUI_V_xx,		3,11,11,L0|L1}, /* 60: 0111 0100 0110 0000 xxxx xxxx			*/
	{SUI_A_xx,		3,11,11,L0|L1}, /* 61: 0111 0100 0110 0001 xxxx xxxx			*/
	{SUI_B_xx,		3,11,11,L0|L1}, /* 62: 0111 0100 0110 0010 xxxx xxxx			*/
	{SUI_C_xx,		3,11,11,L0|L1}, /* 63: 0111 0100 0110 0011 xxxx xxxx			*/
	{SUI_D_xx,		3,11,11,L0|L1}, /* 64: 0111 0100 0110 0100 xxxx xxxx			*/
	{SUI_E_xx,		3,11,11,L0|L1}, /* 65: 0111 0100 0110 0101 xxxx xxxx			*/
	{SUI_H_xx,		3,11,11,L0|L1}, /* 66: 0111 0100 0110 0110 xxxx xxxx			*/
	{SUI_L_xx,		3,11,11,L0|L1}, /* 67: 0111 0100 0110 0111 xxxx xxxx			*/
	{NEI_V_xx,		3,11,11,L0|L1}, /* 68: 0111 0100 0110 1000 xxxx xxxx			*/
	{NEI_A_xx,		3,11,11,L0|L1}, /* 69: 0111 0100 0110 1001 xxxx xxxx			*/
	{NEI_B_xx,		3,11,11,L0|L1}, /* 6a: 0111 0100 0110 1010 xxxx xxxx			*/
	{NEI_C_xx,		3,11,11,L0|L1}, /* 6b: 0111 0100 0110 1011 xxxx xxxx			*/
	{NEI_D_xx,		3,11,11,L0|L1}, /* 6c: 0111 0100 0110 1100 xxxx xxxx			*/
	{NEI_E_xx,		3,11,11,L0|L1}, /* 6d: 0111 0100 0110 1101 xxxx xxxx			*/
	{NEI_H_xx,		3,11,11,L0|L1}, /* 6e: 0111 0100 0110 1110 xxxx xxxx			*/
	{NEI_L_xx,		3,11,11,L0|L1}, /* 6f: 0111 0100 0110 1111 xxxx xxxx			*/

	{SBI_V_xx,		3,11,11,L0|L1}, /* 70: 0111 0100 0111 0000 xxxx xxxx			*/
	{SBI_A_xx,		3,11,11,L0|L1}, /* 71: 0111 0100 0111 0001 xxxx xxxx			*/
	{SBI_B_xx,		3,11,11,L0|L1}, /* 72: 0111 0100 0111 0010 xxxx xxxx			*/
	{SBI_C_xx,		3,11,11,L0|L1}, /* 73: 0111 0100 0111 0011 xxxx xxxx			*/
	{SBI_D_xx,		3,11,11,L0|L1}, /* 74: 0111 0100 0111 0100 xxxx xxxx			*/
	{SBI_E_xx,		3,11,11,L0|L1}, /* 75: 0111 0100 0111 0101 xxxx xxxx			*/
	{SBI_H_xx,		3,11,11,L0|L1}, /* 76: 0111 0100 0111 0110 xxxx xxxx			*/
	{SBI_L_xx,		3,11,11,L0|L1}, /* 77: 0111 0100 0111 0111 xxxx xxxx			*/
	{EQI_V_xx,		3,11,11,L0|L1}, /* 78: 0111 0100 0111 1000 xxxx xxxx			*/
	{EQI_A_xx,		3,11,11,L0|L1}, /* 79: 0111 0100 0111 1001 xxxx xxxx			*/
	{EQI_B_xx,		3,11,11,L0|L1}, /* 7a: 0111 0100 0111 1010 xxxx xxxx			*/
	{EQI_C_xx,		3,11,11,L0|L1}, /* 7b: 0111 0100 0111 1011 xxxx xxxx			*/
	{EQI_D_xx,		3,11,11,L0|L1}, /* 7c: 0111 0100 0111 1100 xxxx xxxx			*/
	{EQI_E_xx,		3,11,11,L0|L1}, /* 7d: 0111 0100 0111 1101 xxxx xxxx			*/
	{EQI_H_xx,		3,11,11,L0|L1}, /* 7e: 0111 0100 0111 1110 xxxx xxxx			*/
	{EQI_L_xx,		3,11,11,L0|L1}, /* 7f: 0111 0100 0111 1111 xxxx xxxx			*/

	{illegal2,		2, 8, 8,L0|L1}, /* 80: 0111 0100 1000 0000						*/
	{illegal2,		2, 8, 8,L0|L1}, /* 81: 0111 0100 1000 0001						*/
	{illegal2,		2, 8, 8,L0|L1}, /* 82: 0111 0100 1000 0010						*/
	{illegal2,		2, 8, 8,L0|L1}, /* 83: 0111 0100 1000 0011						*/
	{illegal2,		2, 8, 8,L0|L1}, /* 84: 0111 0100 1000 0100						*/
	{illegal2,		2, 8, 8,L0|L1}, /* 85: 0111 0100 1000 0101						*/
	{illegal2,		2, 8, 8,L0|L1}, /* 86: 0111 0100 1000 0110						*/
	{illegal2,		2, 8, 8,L0|L1}, /* 87: 0111 0100 1000 0111						*/
	{ANAW_wa,		3,14,11,L0|L1}, /* 88: 0111 0100 1000 1000 oooo oooo			*/
	{illegal2,		2, 8, 8,L0|L1}, /* 89: 0111 0100 1000 1001						*/
	{illegal2,		2, 8, 8,L0|L1}, /* 8a: 0111 0100 1000 1010						*/
	{illegal2,		2, 8, 8,L0|L1}, /* 8b: 0111 0100 1000 1011						*/
	{illegal2,		2, 8, 8,L0|L1}, /* 8c: 0111 0100 1000 1100						*/
	{DAN_EA_BC, 	2,11,11,L0|L1}, /* 8d: 0111 0100 1000 1101						*/
	{DAN_EA_DE, 	2,11,11,L0|L1}, /* 8e: 0111 0100 1000 1110						*/
	{DAN_EA_HL, 	2,11,11,L0|L1}, /* 8f: 0111 0100 1000 1111						*/

	{XRAW_wa,		3,14,11,L0|L1}, /* 90: 0111 0100 1001 0000 oooo oooo			*/
	{illegal2,		2, 8, 8,L0|L1}, /* 91: 0111 0100 1001 0001						*/
	{illegal2,		2, 8, 8,L0|L1}, /* 92: 0111 0100 1001 0010						*/
	{illegal2,		2, 8, 8,L0|L1}, /* 93: 0111 0100 1001 0011						*/
	{illegal2,		2, 8, 8,L0|L1}, /* 94: 0111 0100 1001 0100						*/
	{DXR_EA_BC, 	2,11,11,L0|L1}, /* 95: 0111 0100 1001 0101						*/
	{DXR_EA_DE, 	2,11,11,L0|L1}, /* 96: 0111 0100 1001 0110						*/
	{DXR_EA_HL, 	2,11,11,L0|L1}, /* 97: 0111 0100 1001 0111						*/
	{ORAW_wa,		3,14,11,L0|L1}, /* 98: 0111 0100 1001 1000 oooo oooo			*/
	{illegal2,		2, 8, 8,L0|L1}, /* 99: 0111 0100 1001 1001						*/
	{illegal2,		2, 8, 8,L0|L1}, /* 9a: 0111 0100 1001 1010						*/
	{illegal2,		2, 8, 8,L0|L1}, /* 9b: 0111 0100 1001 1011						*/
	{illegal2,		2, 8, 8,L0|L1}, /* 9c: 0111 0100 1001 1100						*/
	{DOR_EA_BC, 	2,11,11,L0|L1}, /* 9d: 0111 0100 1001 1101						*/
	{DOR_EA_DE, 	2,11,11,L0|L1}, /* 9e: 0111 0100 1001 1110						*/
	{DOR_EA_HL, 	2,11,11,L0|L1}, /* 9f: 0111 0100 1001 1111						*/

	{ADDNCW_wa, 	3,14,11,L0|L1}, /* a0: 0111 0100 1010 0000 oooo oooo			*/
	{illegal2,		2, 8, 8,L0|L1}, /* a1: 0111 0100 1010 0001						*/
	{illegal2,		2, 8, 8,L0|L1}, /* a2: 0111 0100 1010 0010						*/
	{illegal2,		2, 8, 8,L0|L1}, /* a3: 0111 0100 1010 0011						*/
	{illegal2,		2, 8, 8,L0|L1}, /* a4: 0111 0100 1010 0100						*/
	{DADDNC_EA_BC,	2,11,11,L0|L1}, /* a5: 0111 0100 1010 0101						*/
	{DADDNC_EA_DE,	2,11,11,L0|L1}, /* a6: 0111 0100 1010 0110						*/
	{DADDNC_EA_HL,	2,11,11,L0|L1}, /* a7: 0111 0100 1010 0111						*/
	{GTAW_wa,		3,14,11,L0|L1}, /* a8: 0111 0100 1010 1000 oooo oooo			*/
	{illegal2,		2, 8, 8,L0|L1}, /* a9: 0111 0100 1010 1001						*/
	{illegal2,		2, 8, 8,L0|L1}, /* aa: 0111 0100 1010 1010						*/
	{illegal2,		2, 8, 8,L0|L1}, /* ab: 0111 0100 1010 1011						*/
	{illegal2,		2, 8, 8,L0|L1}, /* ac: 0111 0100 1010 1100						*/
	{DGT_EA_BC, 	2,11,11,L0|L1}, /* ad: 0111 0100 1010 1101						*/
	{DGT_EA_DE, 	2,11,11,L0|L1}, /* ae: 0111 0100 1010 1110						*/
	{DGT_EA_HL, 	2,11,11,L0|L1}, /* af: 0111 0100 1010 1111						*/

	{SUBNBW_wa, 	3,14,11,L0|L1}, /* b0: 0111 0100 1011 0000 oooo oooo			*/
	{illegal2,		2, 8, 8,L0|L1}, /* b1: 0111 0100 1011 0001						*/
	{illegal2,		2, 8, 8,L0|L1}, /* b2: 0111 0100 1011 0010						*/
	{illegal2,		2, 8, 8,L0|L1}, /* b3: 0111 0100 1011 0011						*/
	{illegal2,		2, 8, 8,L0|L1}, /* b4: 0111 0100 1011 0100						*/
	{DSUBNB_EA_BC,	2,11,11,L0|L1}, /* b5: 0111 0100 1011 0101						*/
	{DSUBNB_EA_DE,	2,11,11,L0|L1}, /* b6: 0111 0100 1011 0110						*/
	{DSUBNB_EA_HL,	2,11,11,L0|L1}, /* b7: 0111 0100 1011 0111						*/
	{LTAW_wa,		3,14,11,L0|L1}, /* b8: 0111 0100 1011 1000 oooo oooo			*/
	{illegal2,		2, 8, 8,L0|L1}, /* b9: 0111 0100 1011 1001						*/
	{illegal2,		2, 8, 8,L0|L1}, /* ba: 0111 0100 1011 1010						*/
	{illegal2,		2, 8, 8,L0|L1}, /* bb: 0111 0100 1011 1011						*/
	{illegal2,		2, 8, 8,L0|L1}, /* bc: 0111 0100 1011 1100						*/
	{DLT_EA_BC, 	2,11,11,L0|L1}, /* bd: 0111 0100 1011 1101						*/
	{DLT_EA_DE, 	2,11,11,L0|L1}, /* be: 0111 0100 1011 1110						*/
	{DLT_EA_HL, 	2,11,11,L0|L1}, /* bf: 0111 0100 1011 1111						*/

	{ADDW_wa,		3,14,11,L0|L1}, /* c0: 0111 0100 1100 0000 oooo oooo			*/
	{illegal2,		2, 8, 8,L0|L1}, /* c1: 0111 0100 1100 0001						*/
	{illegal2,		2, 8, 8,L0|L1}, /* c2: 0111 0100 1100 0010						*/
	{illegal2,		2, 8, 8,L0|L1}, /* c3: 0111 0100 1100 0011						*/
	{illegal2,		2, 8, 8,L0|L1}, /* c4: 0111 0100 1100 0100						*/
	{DADD_EA_BC,	2,11,11,L0|L1}, /* c5: 0111 0100 1100 0101						*/
	{DADD_EA_DE,	2,11,11,L0|L1}, /* c6: 0111 0100 1100 0110						*/
	{DADD_EA_HL,	2,11,11,L0|L1}, /* c7: 0111 0100 1100 0111						*/
	{ONAW_wa,		3,14,11,L0|L1}, /* c8: 0111 0100 1100 1000 oooo oooo			*/
	{illegal2,		2, 8, 8,L0|L1}, /* c9: 0111 0100 1100 1001						*/
	{illegal2,		2, 8, 8,L0|L1}, /* ca: 0111 0100 1100 1010						*/
	{illegal2,		2, 8, 8,L0|L1}, /* cb: 0111 0100 1100 1011						*/
	{illegal2,		2, 8, 8,L0|L1}, /* cc: 0111 0100 1100 1100						*/
	{DON_EA_BC, 	2,11,11,L0|L1}, /* cd: 0111 0100 1100 1101						*/
	{DON_EA_DE, 	2,11,11,L0|L1}, /* ce: 0111 0100 1100 1110						*/
	{DON_EA_HL, 	2,11,11,L0|L1}, /* cf: 0111 0100 1100 1111						*/

	{ADCW_wa,		3,14,11,L0|L1}, /* d0: 0111 0100 1101 0000 oooo oooo			*/
	{illegal2,		2, 8, 8,L0|L1}, /* d1: 0111 0100 1101 0001						*/
	{illegal2,		2, 8, 8,L0|L1}, /* d2: 0111 0100 1101 0010						*/
	{illegal2,		2, 8, 8,L0|L1}, /* d3: 0111 0100 1101 0011						*/
	{illegal2,		2, 8, 8,L0|L1}, /* d4: 0111 0100 1101 0100						*/
	{DADC_EA_BC,	2,11,11,L0|L1}, /* d5: 0111 0100 1101 0101						*/
	{DADC_EA_DE,	2,11,11,L0|L1}, /* d6: 0111 0100 1101 0110						*/
	{DADC_EA_HL,	2,11,11,L0|L1}, /* d7: 0111 0100 1101 0111						*/
	{OFFAW_wa,		3,14,11,L0|L1}, /* d8: 0111 0100 1101 1000 oooo oooo			*/
	{illegal2,		2, 8, 8,L0|L1}, /* d9: 0111 0100 1101 1001						*/
	{illegal2,		2, 8, 8,L0|L1}, /* da: 0111 0100 1101 1010						*/
	{illegal2,		2, 8, 8,L0|L1}, /* db: 0111 0100 1101 1011						*/
	{illegal2,		2, 8, 8,L0|L1}, /* dc: 0111 0100 1101 1100						*/
	{DOFF_EA_BC,	2,11,11,L0|L1}, /* dd: 0111 0100 1101 1101						*/
	{DOFF_EA_DE,	2,11,11,L0|L1}, /* de: 0111 0100 1101 1110						*/
	{DOFF_EA_HL,	2,11,11,L0|L1}, /* df: 0111 0100 1101 1111						*/

	{SUBW_wa,		3,14,11,L0|L1}, /* e0: 0111 0100 1110 0000 oooo oooo			*/
	{illegal2,		2, 8, 8,L0|L1}, /* e1: 0111 0100 1110 0001						*/
	{illegal2,		2, 8, 8,L0|L1}, /* e2: 0111 0100 1110 0010						*/
	{illegal2,		2, 8, 8,L0|L1}, /* e3: 0111 0100 1110 0011						*/
	{illegal2,		2, 8, 8,L0|L1}, /* e4: 0111 0100 1110 0100						*/
	{DSUB_EA_BC,	2,11,11,L0|L1}, /* e5: 0111 0100 1110 0101						*/
	{DSUB_EA_DE,	2,11,11,L0|L1}, /* e6: 0111 0100 1110 0110						*/
	{DSUB_EA_HL,	2,11,11,L0|L1}, /* e7: 0111 0100 1110 0111						*/
	{NEAW_wa,		3,14,11,L0|L1}, /* e8: 0111 0100 1110 1000 oooo oooo			*/
	{illegal2,		2, 8, 8,L0|L1}, /* e9: 0111 0100 1110 1001						*/
	{illegal2,		2, 8, 8,L0|L1}, /* ea: 0111 0100 1110 1010						*/
	{illegal2,		2, 8, 8,L0|L1}, /* eb: 0111 0100 1110 1011						*/
	{illegal2,		2, 8, 8,L0|L1}, /* ec: 0111 0100 1110 1100						*/
	{DNE_EA_BC, 	2,11,11,L0|L1}, /* ed: 0111 0100 1110 1101						*/
	{DNE_EA_DE, 	2,11,11,L0|L1}, /* ee: 0111 0100 1110 1110						*/
	{DNE_EA_HL, 	2,11,11,L0|L1}, /* ef: 0111 0100 1110 1111						*/

	{SBBW_wa,		3,14,11,L0|L1}, /* f0: 0111 0100 1111 0000 oooo oooo			*/
	{illegal2,		2, 8, 8,L0|L1}, /* f1: 0111 0100 1111 0001						*/
	{illegal2,		2, 8, 8,L0|L1}, /* f2: 0111 0100 1111 0010						*/
	{illegal2,		2, 8, 8,L0|L1}, /* f3: 0111 0100 1111 0011						*/
	{illegal2,		2, 8, 8,L0|L1}, /* f4: 0111 0100 1111 0100						*/
	{DSBB_EA_BC,	2,11,11,L0|L1}, /* f5: 0111 0100 1111 0101						*/
	{DSBB_EA_DE,	2,11,11,L0|L1}, /* f6: 0111 0100 1111 0110						*/
	{DSBB_EA_HL,	2,11,11,L0|L1}, /* f7: 0111 0100 1111 0111						*/
	{EQAW_wa,		3,14,11,L0|L1}, /* f8: 0111 0100 1111 1000 oooo oooo			*/
	{illegal2,		2, 8, 8,L0|L1}, /* f9: 0111 0100 1111 1001						*/
	{illegal2,		2, 8, 8,L0|L1}, /* fa: 0111 0100 1111 1010						*/
	{illegal2,		2, 8, 8,L0|L1}, /* fb: 0111 0100 1111 1011						*/
	{illegal2,		2, 8, 8,L0|L1}, /* fc: 0111 0100 1111 1100						*/
	{DEQ_EA_BC, 	2,11,11,L0|L1}, /* fd: 0111 0100 1111 1101						*/
	{DEQ_EA_DE, 	2,11,11,L0|L1}, /* fe: 0111 0100 1111 1110						*/
	{DEQ_EA_HL, 	2,11,11,L0|L1}	/* ff: 0111 0100 1111 1111						*/
};

/* main opcodes */
static struct opcode_s opXX_7810[256] =
{
	{NOP,			1, 4, 4,L0|L1}, /* 00: 0000 0000								*/
	{LDAW_wa,		2,10,10,L0|L1}, /* 01: 0000 0001 oooo oooo						*/
	{INX_SP,		1, 7, 7,L0|L1}, /* 02: 0000 0010								*/
	{DCX_SP,		1, 7, 7,L0|L1}, /* 03: 0000 0011								*/
	{LXI_S_w,		3,10,10,L0|L1}, /* 04: 0000 0100 llll llll hhhh hhhh			*/
	{ANIW_wa_xx,	3,19,19,L0|L1}, /* 05: 0000 0101 oooo oooo xxxx xxxx			*/
	{illegal,		1, 4, 4,L0|L1}, /* 06:											*/
	{ANI_A_xx,		2, 7, 7,L0|L1}, /* 07: 0000 0111 xxxx xxxx						*/
	{MOV_A_EAH, 	1, 4, 4,L0|L1}, /* 08: 0000 1000								*/
	{MOV_A_EAL, 	1, 4, 4,L0|L1}, /* 09: 0000 1001								*/
	{MOV_A_B,		1, 4, 4,L0|L1}, /* 0a: 0000 1010								*/
	{MOV_A_C,		1, 4, 4,L0|L1}, /* 0b: 0000 1011								*/
	{MOV_A_D,		1, 4, 4,L0|L1}, /* 0c: 0000 1100								*/
	{MOV_A_E,		1, 4, 4,L0|L1}, /* 0d: 0000 1101								*/
	{MOV_A_H,		1, 4, 4,L0|L1}, /* 0e: 0000 1110								*/
	{MOV_A_L,		1, 4, 4,L0|L1}, /* 0f: 0000 1111								*/

	{EXA,			1, 4, 4,L0|L1}, /* 10: 0001 0000								*/
	{EXX,			1, 4, 4,L0|L1}, /* 11: 0001 0001								*/
	{INX_BC,		1, 7, 7,L0|L1}, /* 12: 0001 0010								*/
	{DCX_BC,		1, 7, 7,L0|L1}, /* 13: 0001 0011								*/
	{LXI_B_w,		3,10,10,L0|L1}, /* 14: 0001 0100 llll llll hhhh hhhh			*/
	{ORIW_wa_xx,	3,19,19,L0|L1}, /* 15: 0001 0101 oooo oooo xxxx xxxx			*/
	{XRI_A_xx,		2, 7, 7,L0|L1}, /* 16: 0001 0110 xxxx xxxx						*/
	{ORI_A_xx,		2, 7, 7,L0|L1}, /* 17: 0001 0111 xxxx xxxx						*/
	{MOV_EAH_A, 	1, 4, 4,L0|L1}, /* 18: 0001 1000								*/
	{MOV_EAL_A, 	1, 4, 4,L0|L1}, /* 19: 0001 1001								*/
	{MOV_B_A,		1, 4, 4,L0|L1}, /* 1a: 0001 1010								*/
	{MOV_C_A,		1, 4, 4,L0|L1}, /* 1b: 0001 1011								*/
	{MOV_D_A,		1, 4, 4,L0|L1}, /* 1c: 0001 1100								*/
	{MOV_E_A,		1, 4, 4,L0|L1}, /* 1d: 0001 1101								*/
	{MOV_H_A,		1, 4, 4,L0|L1}, /* 1e: 0001 1110								*/
	{MOV_L_A,		1, 4, 4,L0|L1}, /* 1f: 0001 1111								*/

	{INRW_wa,		2,16,16,L0|L1}, /* 20: 0010 0000 oooo oooo						*/
	{JB,			1, 4, 4,L0|L1}, /* 21: 0010 0001								*/
	{INX_DE,		1, 7, 7,L0|L1}, /* 22: 0010 0010								*/
	{DCX_DE,		1, 7, 7,L0|L1}, /* 23: 0010 0011								*/
	{LXI_D_w,		3,10,10,L0|L1}, /* 24: 0010 0100 llll llll hhhh hhhh			*/
	{GTIW_wa_xx,	3,19,19,L0|L1}, /* 25: 0010 0101 oooo oooo xxxx xxxx			*/
	{ADINC_A_xx,	2, 7, 7,L0|L1}, /* 26: 0010 0110 xxxx xxxx						*/
	{GTI_A_xx,		2, 7, 7,L0|L1}, /* 27: 0010 0111 xxxx xxxx						*/
	{illegal,		1, 4, 4,L0|L1}, /* 28:											*/
	{LDAX_B,		2, 7, 7,L0|L1}, /* 29: 0010 1001 dddd dddd						*/
	{LDAX_D,		2, 7, 7,L0|L1}, /* 2a: 0010 1010 dddd dddd						*/
	{LDAX_H,		2, 7, 7,L0|L1}, /* 2b: 0010 1011 dddd dddd						*/
	{LDAX_Dp,		2, 7, 7,L0|L1}, /* 2c: 0010 1100 dddd dddd						*/
	{LDAX_Hp,		2, 7, 7,L0|L1}, /* 2d: 0010 1101 dddd dddd						*/
	{LDAX_Dm,		2, 7, 7,L0|L1}, /* 2e: 0010 1110 dddd dddd						*/
	{LDAX_Hm,		2, 7, 7,L0|L1}, /* 2f: 0010 1111 dddd dddd						*/

	{DCRW_wa,		2,16,16,L0|L1}, /* 30: 0011 0000 oooo oooo						*/
	{BLOCK, 		1,13,13,L0|L1}, /* 31: 0011 0001								*/	/* 7810 */
	{INX_HL,		1, 7, 7,L0|L1}, /* 32: 0011 0010								*/
	{DCX_HL,		1, 7, 7,L0|L1}, /* 33: 0011 0011								*/
	{LXI_H_w,		3,10,10,   L1}, /* 34: 0011 0100 llll llll hhhh hhhh			*/
	{LTIW_wa_xx,	3,19,19,L0|L1}, /* 35: 0011 0101 oooo oooo xxxx xxxx			*/
	{SUINB_A_xx,	2, 7, 7,L0|L1}, /* 36: 0011 0110 xxxx xxxx						*/
	{LTI_A_xx,		2, 7, 7,L0|L1}, /* 37: 0011 0111 xxxx xxxx						*/
	{illegal,		1, 4, 4,L0|L1}, /* 38:											*/
	{STAX_B,		2, 7, 7,L0|L1}, /* 39: 0011 1001 dddd dddd						*/
	{STAX_D,		2, 7, 7,L0|L1}, /* 3a: 0011 1010 dddd dddd						*/
	{STAX_H,		2, 7, 7,L0|L1}, /* 3b: 0011 1011 dddd dddd						*/
	{STAX_Dp,		2, 7, 7,L0|L1}, /* 3c: 0011 1100 dddd dddd						*/
	{STAX_Hp,		2, 7, 7,L0|L1}, /* 3d: 0011 1101 dddd dddd						*/
	{STAX_Dm,		2, 7, 7,L0|L1}, /* 3e: 0011 1110 dddd dddd						*/
	{STAX_Hm,		2, 7, 7,L0|L1}, /* 3f: 0011 1111 dddd dddd						*/

	{CALL_w,		3,16,16,L0|L1}, /* 40: 0100 0000 llll llll hhhh hhhh			*/
	{INR_A, 		1, 4, 4,L0|L1}, /* 41: 0100 0001								*/
	{INR_B, 		1, 4, 4,L0|L1}, /* 42: 0100 0010								*/
	{INR_C, 		1, 4, 4,L0|L1}, /* 43: 0100 0011								*/
	{LXI_EA_s,		3,10,10,L0|L1}, /* 44: 0100 0100 llll llll hhhh hhhh			*/
	{ONIW_wa_xx,	3,19,19,L0|L1}, /* 45: 0100 0101 oooo oooo xxxx xxxx			*/
	{ADI_A_xx,		2, 7, 7,L0|L1}, /* 46: 0100 0110 xxxx xxxx						*/
	{ONI_A_xx,		2, 7, 7,L0|L1}, /* 47: 0100 0111 xxxx xxxx						*/
	{PRE_48,		1, 0, 0,L0|L1}, /* 48: prefix									*/
	{MVIX_BC_xx,	2,10,10,L0|L1}, /* 49: 0100 1001 xxxx xxxx						*/
	{MVIX_DE_xx,	2,10,10,L0|L1}, /* 4a: 0100 1010 xxxx xxxx						*/
	{MVIX_HL_xx,	2,10,10,L0|L1}, /* 4b: 0100 1011 xxxx xxxx						*/
	{PRE_4C,		1, 0, 0,L0|L1}, /* 4c: prefix									*/
	{PRE_4D,		1, 4, 4,L0|L1}, /* 4d: prefix									*/
	{JRE,			2,10,10,L0|L1}, /* 4e: 0100 111d dddd dddd						*/
	{JRE,			2,10,10,L0|L1}, /* 4f: 0100 111d dddd dddd						*/

	{EXH,			1, 4, 4,L0|L1}, /* 50: 0101 0000								*/	/* 7810 */
	{DCR_A, 		1, 4, 4,L0|L1}, /* 51: 0101 0001								*/
	{DCR_B, 		1, 4, 4,L0|L1}, /* 52: 0101 0010								*/
	{DCR_C, 		1, 4, 4,L0|L1}, /* 53: 0101 0011								*/
	{JMP_w, 		3,10,10,L0|L1}, /* 54: 0101 0100 llll llll hhhh hhhh			*/
	{OFFIW_wa_xx,	3,19,19,L0|L1}, /* 55: 0101 0101 oooo oooo xxxx xxxx			*/
	{ACI_A_xx,		2, 7, 7,L0|L1}, /* 56: 0101 0110 xxxx xxxx						*/
	{OFFI_A_xx, 	2, 7, 7,L0|L1}, /* 57: 0101 0111 xxxx xxxx						*/
	{BIT_0_wa,		2,10,10,L0|L1}, /* 58: 0101 1000 oooo oooo						*/	/* 7810 */
	{BIT_1_wa,		2,10,10,L0|L1}, /* 59: 0101 1001 oooo oooo						*/	/* 7810 */
	{BIT_2_wa,		2,10,10,L0|L1}, /* 5a: 0101 1010 oooo oooo						*/	/* 7810 */
	{BIT_3_wa,		2,10,10,L0|L1}, /* 5b: 0101 1011 oooo oooo						*/	/* 7810 */
	{BIT_4_wa,		2,10,10,L0|L1}, /* 5c: 0101 1100 oooo oooo						*/	/* 7810 */
	{BIT_5_wa,		2,10,10,L0|L1}, /* 5d: 0101 1101 oooo oooo						*/	/* 7810 */
	{BIT_6_wa,		2,10,10,L0|L1}, /* 5e: 0101 1110 oooo oooo						*/	/* 7810 */
	{BIT_7_wa,		2,10,10,L0|L1}, /* 5f: 0101 1111 oooo oooo						*/	/* 7810 */

	{PRE_60,		1, 0, 0,L0|L1}, /* 60:											*/
	{DAA,			1, 4, 4,L0|L1}, /* 61: 0110 0001								*/
	{RETI,			1,13,13,L0|L1}, /* 62: 0110 0010								*/
	{STAW_wa,		2,10,10,L0|L1}, /* 63: 0110 0011 oooo oooo						*/
	{PRE_64,		1, 0, 0,L0|L1}, /* 64:											*/
	{NEIW_wa_xx,	3,19,19,L0|L1}, /* 65: 0110 0101 oooo oooo xxxx xxxx			*/
	{SUI_A_xx,		2, 7, 7,L0|L1}, /* 66: 0110 0110 xxxx xxxx						*/
	{NEI_A_xx,		2, 7, 7,L0|L1}, /* 67: 0110 0111 xxxx xxxx						*/
	{MVI_V_xx,		2, 7, 7,L0|L1}, /* 68: 0110 1000 xxxx xxxx						*/
	{MVI_A_xx,		2, 7, 7,L0	 }, /* 69: 0110 1001 xxxx xxxx						*/
	{MVI_B_xx,		2, 7, 7,L0|L1}, /* 6a: 0110 1010 xxxx xxxx						*/
	{MVI_C_xx,		2, 7, 7,L0|L1}, /* 6b: 0110 1011 xxxx xxxx						*/
	{MVI_D_xx,		2, 7, 7,L0|L1}, /* 6c: 0110 1100 xxxx xxxx						*/
	{MVI_E_xx,		2, 7, 7,L0|L1}, /* 6d: 0110 1101 xxxx xxxx						*/
	{MVI_H_xx,		2, 7, 7,L0|L1}, /* 6e: 0110 1110 xxxx xxxx						*/
	{MVI_L_xx,		2, 7, 7,   L1}, /* 6f: 0110 1111 xxxx xxxx						*/

	{PRE_70,		1, 0, 0,L0|L1}, /* 70:											*/
	{MVIW_wa_xx,	3,13,13,L0|L1}, /* 71: 0111 0001 oooo oooo xxxx xxxx			*/
	{SOFTI, 		1,16,16,L0|L1}, /* 72: 0111 0010								*/
	{illegal,		1, 0, 0,L0|L1}, /* 73:											*/
	{PRE_74,		1, 0, 0,L0|L1}, /* 74: prefix									*/
	{EQIW_wa_xx,	3,19,19,L0|L1}, /* 75: 0111 0101 oooo oooo xxxx xxxx			*/
	{SBI_A_xx,		2, 7, 7,L0|L1}, /* 76: 0111 0110 xxxx xxxx						*/
	{EQI_A_xx,		2, 7, 7,L0|L1}, /* 77: 0111 0111 xxxx xxxx						*/
	{CALF,			2,13,13,L0|L1}, /* 78: 0111 1xxx xxxx xxxx						*/
	{CALF,			2,13,13,L0|L1}, /* 79: 0111 1xxx xxxx xxxx						*/
	{CALF,			2,13,13,L0|L1}, /* 7a: 0111 1xxx xxxx xxxx						*/
	{CALF,			2,13,13,L0|L1}, /* 7b: 0111 1xxx xxxx xxxx						*/
	{CALF,			2,13,13,L0|L1}, /* 7c: 0111 1xxx xxxx xxxx						*/
	{CALF,			2,13,13,L0|L1}, /* 7d: 0111 1xxx xxxx xxxx						*/
	{CALF,			2,13,13,L0|L1}, /* 7e: 0111 1xxx xxxx xxxx						*/
	{CALF,			2,13,13,L0|L1}, /* 7f: 0111 1xxx xxxx xxxx						*/

	{CALT,			1,16,16,L0|L1}, /* 80: 100x xxxx								*/
	{CALT,			1,16,16,L0|L1}, /* 81: 100x xxxx								*/
	{CALT,			1,16,16,L0|L1}, /* 82: 100x xxxx								*/
	{CALT,			1,16,16,L0|L1}, /* 83: 100x xxxx								*/
	{CALT,			1,16,16,L0|L1}, /* 84: 100x xxxx								*/
	{CALT,			1,16,16,L0|L1}, /* 85: 100x xxxx								*/
	{CALT,			1,16,16,L0|L1}, /* 86: 100x xxxx								*/
	{CALT,			1,16,16,L0|L1}, /* 87: 100x xxxx								*/
	{CALT,			1,16,16,L0|L1}, /* 88: 100x xxxx								*/
	{CALT,			1,16,16,L0|L1}, /* 89: 100x xxxx								*/
	{CALT,			1,16,16,L0|L1}, /* 8a: 100x xxxx								*/
	{CALT,			1,16,16,L0|L1}, /* 8b: 100x xxxx								*/
	{CALT,			1,16,16,L0|L1}, /* 8c: 100x xxxx								*/
	{CALT,			1,16,16,L0|L1}, /* 8d: 100x xxxx								*/
	{CALT,			1,16,16,L0|L1}, /* 8e: 100x xxxx								*/
	{CALT,			1,16,16,L0|L1}, /* 8f: 100x xxxx								*/

	{CALT,			1,16,16,L0|L1}, /* 90: 100x xxxx								*/
	{CALT,			1,16,16,L0|L1}, /* 91: 100x xxxx								*/
	{CALT,			1,16,16,L0|L1}, /* 92: 100x xxxx								*/
	{CALT,			1,16,16,L0|L1}, /* 93: 100x xxxx								*/
	{CALT,			1,16,16,L0|L1}, /* 94: 100x xxxx								*/
	{CALT,			1,16,16,L0|L1}, /* 95: 100x xxxx								*/
	{CALT,			1,16,16,L0|L1}, /* 96: 100x xxxx								*/
	{CALT,			1,16,16,L0|L1}, /* 97: 100x xxxx								*/
	{CALT,			1,16,16,L0|L1}, /* 98: 100x xxxx								*/
	{CALT,			1,16,16,L0|L1}, /* 99: 100x xxxx								*/
	{CALT,			1,16,16,L0|L1}, /* 9a: 100x xxxx								*/
	{CALT,			1,16,16,L0|L1}, /* 9b: 100x xxxx								*/
	{CALT,			1,16,16,L0|L1}, /* 9c: 100x xxxx								*/
	{CALT,			1,16,16,L0|L1}, /* 9d: 100x xxxx								*/
	{CALT,			1,16,16,L0|L1}, /* 9e: 100x xxxx								*/
	{CALT,			1,16,16,L0|L1}, /* 9f: 100x xxxx								*/

	{POP_VA,		1,10,10,L0|L1}, /* a0: 1010 0000								*/
	{POP_BC,		1,10,10,L0|L1}, /* a1: 1010 0001								*/
	{POP_DE,		1,10,10,L0|L1}, /* a2: 1010 0010								*/
	{POP_HL,		1,10,10,L0|L1}, /* a3: 1010 0011								*/
	{POP_EA,		1,10,10,L0|L1}, /* a4: 1010 0100								*/
	{DMOV_EA_BC,	1, 4, 4,L0|L1}, /* a5: 1010 0101								*/
	{DMOV_EA_DE,	1, 4, 4,L0|L1}, /* a6: 1010 0110								*/
	{DMOV_EA_HL,	1, 4, 4,L0|L1}, /* a7: 1010 0111								*/
	{INX_EA,		1, 7, 7,L0|L1}, /* a8: 1010 1000								*/
	{DCX_EA,		1, 7, 7,L0|L1}, /* a9: 1010 1001								*/
	{EI,			1, 4, 4,L0|L1}, /* aa: 1010 1010								*/
	{LDAX_D_xx, 	2, 7, 7,L0|L1}, /* ab: 1010 1011 dddd dddd						*/
	{LDAX_H_A,		1, 7, 7,L0|L1}, /* ac: 1010 1100								*/
	{LDAX_H_B,		1, 7, 7,L0|L1}, /* ad: 1010 1101								*/
	{LDAX_H_EA, 	1, 7, 7,L0|L1}, /* ae: 1010 1110								*/
	{LDAX_H_xx, 	2, 7, 7,L0|L1}, /* af: 1010 1111 dddd dddd						*/

	{PUSH_VA,		1,13,13,L0|L1}, /* b0: 1011 0000								*/
	{PUSH_BC,		1,13,13,L0|L1}, /* b1: 1011 0001								*/
	{PUSH_DE,		1,13,13,L0|L1}, /* b2: 1011 0010								*/
	{PUSH_HL,		1,13,13,L0|L1}, /* b3: 1011 0011								*/
	{PUSH_EA,		1,13,13,L0|L1}, /* b4: 1011 0100								*/
	{DMOV_BC_EA,	1, 4, 4,L0|L1}, /* b5: 1011 0101								*/
	{DMOV_DE_EA,	1, 4, 4,L0|L1}, /* b6: 1011 0110								*/
	{DMOV_HL_EA,	1, 4, 4,L0|L1}, /* b7: 1011 0111								*/
	{RET,			1,10,10,L0|L1}, /* b8: 1011 1000								*/
	{RETS,			1,10,10,L0|L1}, /* b9: 1011 1001								*/
	{DI,			1, 4, 4,L0|L1}, /* ba: 1011 1010								*/
	{STAX_D_xx, 	2, 7, 7,L0|L1}, /* bb: 1011 1011 dddd dddd						*/
	{STAX_H_A,		1, 7, 7,L0|L1}, /* bc: 1011 1100								*/
	{STAX_H_B,		1, 7, 7,L0|L1}, /* bd: 1011 1101								*/
	{STAX_H_EA, 	1, 7, 7,L0|L1}, /* be: 1011 1110								*/
	{STAX_H_xx, 	2, 7, 7,L0|L1}, /* bf: 1011 1111 dddd dddd						*/

	{JR,			1,10,10,L0|L1}, /* c0: 1100 0000								*/
	{JR,			1,10,10,L0|L1}, /* c1: 1100 0001								*/
	{JR,			1,10,10,L0|L1}, /* c2: 1100 0010								*/
	{JR,			1,10,10,L0|L1}, /* c3: 1100 0011								*/
	{JR,			1,10,10,L0|L1}, /* c4: 1100 0100								*/
	{JR,			1,10,10,L0|L1}, /* c5: 1100 0101								*/
	{JR,			1,10,10,L0|L1}, /* c6: 1100 0110								*/
	{JR,			1,10,10,L0|L1}, /* c7: 1100 0111								*/
	{JR,			1,10,10,L0|L1}, /* c8: 1100 1000								*/
	{JR,			1,10,10,L0|L1}, /* c9: 1100 1001								*/
	{JR,			1,10,10,L0|L1}, /* ca: 1100 1010								*/
	{JR,			1,10,10,L0|L1}, /* cb: 1100 1011								*/
	{JR,			1,10,10,L0|L1}, /* cc: 1100 1100								*/
	{JR,			1,10,10,L0|L1}, /* cd: 1100 1101								*/
	{JR,			1,10,10,L0|L1}, /* ce: 1100 1110								*/
	{JR,			1,10,10,L0|L1}, /* cf: 1100 1111								*/

	{JR,			1,10,10,L0|L1}, /* d0: 1101 0000								*/
	{JR,			1,10,10,L0|L1}, /* d1: 1101 0001								*/
	{JR,			1,10,10,L0|L1}, /* d2: 1101 0010								*/
	{JR,			1,10,10,L0|L1}, /* d3: 1101 0011								*/
	{JR,			1,10,10,L0|L1}, /* d4: 1101 0100								*/
	{JR,			1,10,10,L0|L1}, /* d5: 1101 0101								*/
	{JR,			1,10,10,L0|L1}, /* d6: 1101 0110								*/
	{JR,			1,10,10,L0|L1}, /* d7: 1101 0111								*/
	{JR,			1,10,10,L0|L1}, /* d8: 1101 1000								*/
	{JR,			1,10,10,L0|L1}, /* d9: 1101 1001								*/
	{JR,			1,10,10,L0|L1}, /* da: 1101 1010								*/
	{JR,			1,10,10,L0|L1}, /* db: 1101 1011								*/
	{JR,			1,10,10,L0|L1}, /* dc: 1101 1100								*/
	{JR,			1,10,10,L0|L1}, /* dd: 1101 1101								*/
	{JR,			1,10,10,L0|L1}, /* de: 1101 1110								*/
	{JR,			1,10,10,L0|L1}, /* df: 1101 1111								*/

	{JR,			1,10,10,L0|L1}, /* e0: 1110 0000								*/
	{JR,			1,10,10,L0|L1}, /* e1: 1110 0001								*/
	{JR,			1,10,10,L0|L1}, /* e2: 1110 0010								*/
	{JR,			1,10,10,L0|L1}, /* e3: 1110 0011								*/
	{JR,			1,10,10,L0|L1}, /* e4: 1110 0100								*/
	{JR,			1,10,10,L0|L1}, /* e5: 1110 0101								*/
	{JR,			1,10,10,L0|L1}, /* e6: 1110 0110								*/
	{JR,			1,10,10,L0|L1}, /* e7: 1110 0111								*/
	{JR,			1,10,10,L0|L1}, /* e8: 1110 1000								*/
	{JR,			1,10,10,L0|L1}, /* e9: 1110 1001								*/
	{JR,			1,10,10,L0|L1}, /* ea: 1110 1010								*/
	{JR,			1,10,10,L0|L1}, /* eb: 1110 1011								*/
	{JR,			1,10,10,L0|L1}, /* ec: 1110 1100								*/
	{JR,			1,10,10,L0|L1}, /* ed: 1110 1101								*/
	{JR,			1,10,10,L0|L1}, /* ee: 1110 1110								*/
	{JR,			1,10,10,L0|L1}, /* ef: 1110 1111								*/

	{JR,			1,10,10,L0|L1}, /* f0: 1111 0000								*/
	{JR,			1,10,10,L0|L1}, /* f1: 1111 0001								*/
	{JR,			1,10,10,L0|L1}, /* f2: 1111 0010								*/
	{JR,			1,10,10,L0|L1}, /* f3: 1111 0011								*/
	{JR,			1,10,10,L0|L1}, /* f4: 1111 0100								*/
	{JR,			1,10,10,L0|L1}, /* f5: 1111 0101								*/
	{JR,			1,10,10,L0|L1}, /* f6: 1111 0110								*/
	{JR,			1,10,10,L0|L1}, /* f7: 1111 0111								*/
	{JR,			1,10,10,L0|L1}, /* f8: 1111 1000								*/
	{JR,			1,10,10,L0|L1}, /* f9: 1111 1001								*/
	{JR,			1,10,10,L0|L1}, /* fa: 1111 1010								*/
	{JR,			1,10,10,L0|L1}, /* fb: 1111 1011								*/
	{JR,			1,10,10,L0|L1}, /* fc: 1111 1100								*/
	{JR,			1,10,10,L0|L1}, /* fd: 1111 1101								*/
	{JR,			1,10,10,L0|L1}, /* fe: 1111 1110								*/
	{JR,			1,10,10,L0|L1}	/* ff: 1111 1111								*/
};

static struct opcode_s opXX_7807[256] =
{
	{NOP,			1, 4, 4,L0|L1}, /* 00: 0000 0000								*/
	{LDAW_wa,		2,10,10,L0|L1}, /* 01: 0000 0001 oooo oooo						*/
	{INX_SP,		1, 7, 7,L0|L1}, /* 02: 0000 0010								*/
	{DCX_SP,		1, 7, 7,L0|L1}, /* 03: 0000 0011								*/
	{LXI_S_w,		3,10,10,L0|L1}, /* 04: 0000 0100 llll llll hhhh hhhh			*/
	{ANIW_wa_xx,	3,19,19,L0|L1}, /* 05: 0000 0101 oooo oooo xxxx xxxx			*/
	{illegal,		1, 4, 4,L0|L1}, /* 06:											*/
	{ANI_A_xx,		2, 7, 7,L0|L1}, /* 07: 0000 0111 xxxx xxxx						*/
	{MOV_A_EAH, 	1, 4, 4,L0|L1}, /* 08: 0000 1000								*/
	{MOV_A_EAL, 	1, 4, 4,L0|L1}, /* 09: 0000 1001								*/
	{MOV_A_B,		1, 4, 4,L0|L1}, /* 0a: 0000 1010								*/
	{MOV_A_C,		1, 4, 4,L0|L1}, /* 0b: 0000 1011								*/
	{MOV_A_D,		1, 4, 4,L0|L1}, /* 0c: 0000 1100								*/
	{MOV_A_E,		1, 4, 4,L0|L1}, /* 0d: 0000 1101								*/
	{MOV_A_H,		1, 4, 4,L0|L1}, /* 0e: 0000 1110								*/
	{MOV_A_L,		1, 4, 4,L0|L1}, /* 0f: 0000 1111								*/

	{illegal,		1,13, 4,L0|L1}, /* 10: 0001 0000								*/	/* 7807 */
	{illegal,		1,13, 4,L0|L1}, /* 11: 0001 0001								*/	/* 7807 */
	{INX_BC,		1, 7, 7,L0|L1}, /* 12: 0001 0010								*/
	{DCX_BC,		1, 7, 7,L0|L1}, /* 13: 0001 0011								*/
	{LXI_B_w,		3,10,10,L0|L1}, /* 14: 0001 0100 llll llll hhhh hhhh			*/
	{ORIW_wa_xx,	3,19,19,L0|L1}, /* 15: 0001 0101 oooo oooo xxxx xxxx			*/
	{XRI_A_xx,		2, 7, 7,L0|L1}, /* 16: 0001 0110 xxxx xxxx						*/
	{ORI_A_xx,		2, 7, 7,L0|L1}, /* 17: 0001 0111 xxxx xxxx						*/
	{MOV_EAH_A, 	1, 4, 4,L0|L1}, /* 18: 0001 1000								*/
	{MOV_EAL_A, 	1, 4, 4,L0|L1}, /* 19: 0001 1001								*/
	{MOV_B_A,		1, 4, 4,L0|L1}, /* 1a: 0001 1010								*/
	{MOV_C_A,		1, 4, 4,L0|L1}, /* 1b: 0001 1011								*/
	{MOV_D_A,		1, 4, 4,L0|L1}, /* 1c: 0001 1100								*/
	{MOV_E_A,		1, 4, 4,L0|L1}, /* 1d: 0001 1101								*/
	{MOV_H_A,		1, 4, 4,L0|L1}, /* 1e: 0001 1110								*/
	{MOV_L_A,		1, 4, 4,L0|L1}, /* 1f: 0001 1111								*/

	{INRW_wa,		2,16,16,L0|L1}, /* 20: 0010 0000 oooo oooo						*/
	{JB,			1, 4, 4,L0|L1}, /* 21: 0010 0001								*/
	{INX_DE,		1, 7, 7,L0|L1}, /* 22: 0010 0010								*/
	{DCX_DE,		1, 7, 7,L0|L1}, /* 23: 0010 0011								*/
	{LXI_D_w,		3,10,10,L0|L1}, /* 24: 0010 0100 llll llll hhhh hhhh			*/
	{GTIW_wa_xx,	3,19,19,L0|L1}, /* 25: 0010 0101 oooo oooo xxxx xxxx			*/
	{ADINC_A_xx,	2, 7, 7,L0|L1}, /* 26: 0010 0110 xxxx xxxx						*/
	{GTI_A_xx,		2, 7, 7,L0|L1}, /* 27: 0010 0111 xxxx xxxx						*/
	{illegal,		1, 4, 4,L0|L1}, /* 28:											*/
	{LDAX_B,		2, 7, 7,L0|L1}, /* 29: 0010 1001 dddd dddd						*/
	{LDAX_D,		2, 7, 7,L0|L1}, /* 2a: 0010 1010 dddd dddd						*/
	{LDAX_H,		2, 7, 7,L0|L1}, /* 2b: 0010 1011 dddd dddd						*/
	{LDAX_Dp,		2, 7, 7,L0|L1}, /* 2c: 0010 1100 dddd dddd						*/
	{LDAX_Hp,		2, 7, 7,L0|L1}, /* 2d: 0010 1101 dddd dddd						*/
	{LDAX_Dm,		2, 7, 7,L0|L1}, /* 2e: 0010 1110 dddd dddd						*/
	{LDAX_Hm,		2, 7, 7,L0|L1}, /* 2f: 0010 1111 dddd dddd						*/

	{DCRW_wa,		2,16,16,L0|L1}, /* 30: 0011 0000 oooo oooo						*/
	{illegal,		2, 8, 8,L0|L1}, /* 31: 0011 0001 bbbb bbbb						*/	/* 7807 */
	{INX_HL,		1, 7, 7,L0|L1}, /* 32: 0011 0010								*/
	{DCX_HL,		1, 7, 7,L0|L1}, /* 33: 0011 0011								*/
	{LXI_H_w,		3,10,10,   L1}, /* 34: 0011 0100 llll llll hhhh hhhh			*/
	{LTIW_wa_xx,	3,19,19,L0|L1}, /* 35: 0011 0101 oooo oooo xxxx xxxx			*/
	{SUINB_A_xx,	2, 7, 7,L0|L1}, /* 36: 0011 0110 xxxx xxxx						*/
	{LTI_A_xx,		2, 7, 7,L0|L1}, /* 37: 0011 0111 xxxx xxxx						*/
	{illegal,		1, 4, 4,L0|L1}, /* 38:											*/
	{STAX_B,		2, 7, 7,L0|L1}, /* 39: 0011 1001 dddd dddd						*/
	{STAX_D,		2, 7, 7,L0|L1}, /* 3a: 0011 1010 dddd dddd						*/
	{STAX_H,		2, 7, 7,L0|L1}, /* 3b: 0011 1011 dddd dddd						*/
	{STAX_Dp,		2, 7, 7,L0|L1}, /* 3c: 0011 1100 dddd dddd						*/
	{STAX_Hp,		2, 7, 7,L0|L1}, /* 3d: 0011 1101 dddd dddd						*/
	{STAX_Dm,		2, 7, 7,L0|L1}, /* 3e: 0011 1110 dddd dddd						*/
	{STAX_Hm,		2, 7, 7,L0|L1}, /* 3f: 0011 1111 dddd dddd						*/

	{CALL_w,		3,16,16,L0|L1}, /* 40: 0100 0000 llll llll hhhh hhhh			*/
	{INR_A, 		1, 4, 4,L0|L1}, /* 41: 0100 0001								*/
	{INR_B, 		1, 4, 4,L0|L1}, /* 42: 0100 0010								*/
	{INR_C, 		1, 4, 4,L0|L1}, /* 43: 0100 0011								*/
	{LXI_EA_s,		3,10,10,L0|L1}, /* 44: 0100 0100 llll llll hhhh hhhh			*/
	{ONIW_wa_xx,	3,19,19,L0|L1}, /* 45: 0100 0101 oooo oooo xxxx xxxx			*/
	{ADI_A_xx,		2, 7, 7,L0|L1}, /* 46: 0100 0110 xxxx xxxx						*/
	{ONI_A_xx,		2, 7, 7,L0|L1}, /* 47: 0100 0111 xxxx xxxx						*/
	{PRE_48,		1, 0, 0,L0|L1}, /* 48: prefix									*/
	{MVIX_BC_xx,	2,10,10,L0|L1}, /* 49: 0100 1001 xxxx xxxx						*/
	{MVIX_DE_xx,	2,10,10,L0|L1}, /* 4a: 0100 1010 xxxx xxxx						*/
	{MVIX_HL_xx,	2,10,10,L0|L1}, /* 4b: 0100 1011 xxxx xxxx						*/
	{PRE_4C,		1, 0, 0,L0|L1}, /* 4c: prefix									*/
	{PRE_4D,		1, 4, 4,L0|L1}, /* 4d: prefix									*/
	{JRE,			2,10,10,L0|L1}, /* 4e: 0100 111d dddd dddd						*/
	{JRE,			2,10,10,L0|L1}, /* 4f: 0100 111d dddd dddd						*/

	{SKN_bit,		2,13,13,L0|L1}, /* 50: 0101 0000 bbbb bbbb						*/	/* 7807 */
	{DCR_A, 		1, 4, 4,L0|L1}, /* 51: 0101 0001								*/
	{DCR_B, 		1, 4, 4,L0|L1}, /* 52: 0101 0010								*/
	{DCR_C, 		1, 4, 4,L0|L1}, /* 53: 0101 0011								*/
	{JMP_w, 		3,10,10,L0|L1}, /* 54: 0101 0100 llll llll hhhh hhhh			*/
	{OFFIW_wa_xx,	3,19,19,L0|L1}, /* 55: 0101 0101 oooo oooo xxxx xxxx			*/
	{ACI_A_xx,		2, 7, 7,L0|L1}, /* 56: 0101 0110 xxxx xxxx						*/
	{OFFI_A_xx, 	2, 7, 7,L0|L1}, /* 57: 0101 0111 xxxx xxxx						*/
	{SETB,			2,13,13,L0|L1}, /* 58: 0101 1000 bbbb bbbb						*/	/* 7807 */
	{illegal,		2, 8, 8,L0|L1}, /* 59: 0101 1001 bbbb bbbb						*/	/* 7807 */
	{illegal,		2, 8, 8,L0|L1}, /* 5a: 0101 1010 bbbb bbbb						*/	/* 7807 */
	{CLR,			2,13,13,L0|L1}, /* 5b: 0101 1011 bbbb bbbb						*/	/* 7807 */
	{illegal,		2, 8, 8,L0|L1}, /* 5c: 0101 1100 bbbb bbbb						*/	/* 7807 */
	{SK_bit,		2,10,10,L0|L1}, /* 5d: 0101 1101 bbbb bbbb						*/	/* 7807 */
	{illegal,		2, 8, 8,L0|L1}, /* 5e: 0101 1110 bbbb bbbb						*/	/* 7807 */
	{illegal,		2, 8, 8,L0|L1}, /* 5f: 0101 1111 bbbb bbbb						*/	/* 7807 */

	{PRE_60,		1, 0, 0,L0|L1}, /* 60:											*/
	{DAA,			1, 4, 4,L0|L1}, /* 61: 0110 0001								*/
	{RETI,			1,13,13,L0|L1}, /* 62: 0110 0010								*/
	{STAW_wa,		2,10,10,L0|L1}, /* 63: 0110 0011 oooo oooo						*/
	{PRE_64,		1, 0, 0,L0|L1}, /* 64:											*/
	{NEIW_wa_xx,	3,19,19,L0|L1}, /* 65: 0110 0101 oooo oooo xxxx xxxx			*/
	{SUI_A_xx,		2, 7, 7,L0|L1}, /* 66: 0110 0110 xxxx xxxx						*/
	{NEI_A_xx,		2, 7, 7,L0|L1}, /* 67: 0110 0111 xxxx xxxx						*/
	{MVI_V_xx,		2, 7, 7,L0|L1}, /* 68: 0110 1000 xxxx xxxx						*/
	{MVI_A_xx,		2, 7, 7,L0	 }, /* 69: 0110 1001 xxxx xxxx						*/
	{MVI_B_xx,		2, 7, 7,L0|L1}, /* 6a: 0110 1010 xxxx xxxx						*/
	{MVI_C_xx,		2, 7, 7,L0|L1}, /* 6b: 0110 1011 xxxx xxxx						*/
	{MVI_D_xx,		2, 7, 7,L0|L1}, /* 6c: 0110 1100 xxxx xxxx						*/
	{MVI_E_xx,		2, 7, 7,L0|L1}, /* 6d: 0110 1101 xxxx xxxx						*/
	{MVI_H_xx,		2, 7, 7,L0|L1}, /* 6e: 0110 1110 xxxx xxxx						*/
	{MVI_L_xx,		2, 7, 7,   L1}, /* 6f: 0110 1111 xxxx xxxx						*/

	{PRE_70,		1, 0, 0,L0|L1}, /* 70:											*/
	{MVIW_wa_xx,	3,13,13,L0|L1}, /* 71: 0111 0001 oooo oooo xxxx xxxx			*/
	{SOFTI, 		1,16,16,L0|L1}, /* 72: 0111 0010								*/
	{illegal,		1, 0, 0,L0|L1}, /* 73:											*/
	{PRE_74,		1, 0, 0,L0|L1}, /* 74: prefix									*/
	{EQIW_wa_xx,	3,19,19,L0|L1}, /* 75: 0111 0101 oooo oooo xxxx xxxx			*/
	{SBI_A_xx,		2, 7, 7,L0|L1}, /* 76: 0111 0110 xxxx xxxx						*/
	{EQI_A_xx,		2, 7, 7,L0|L1}, /* 77: 0111 0111 xxxx xxxx						*/
	{CALF,			2,13,13,L0|L1}, /* 78: 0111 1xxx xxxx xxxx						*/
	{CALF,			2,13,13,L0|L1}, /* 79: 0111 1xxx xxxx xxxx						*/
	{CALF,			2,13,13,L0|L1}, /* 7a: 0111 1xxx xxxx xxxx						*/
	{CALF,			2,13,13,L0|L1}, /* 7b: 0111 1xxx xxxx xxxx						*/
	{CALF,			2,13,13,L0|L1}, /* 7c: 0111 1xxx xxxx xxxx						*/
	{CALF,			2,13,13,L0|L1}, /* 7d: 0111 1xxx xxxx xxxx						*/
	{CALF,			2,13,13,L0|L1}, /* 7e: 0111 1xxx xxxx xxxx						*/
	{CALF,			2,13,13,L0|L1}, /* 7f: 0111 1xxx xxxx xxxx						*/

	{CALT,			1,16,16,L0|L1}, /* 80: 100x xxxx								*/
	{CALT,			1,16,16,L0|L1}, /* 81: 100x xxxx								*/
	{CALT,			1,16,16,L0|L1}, /* 82: 100x xxxx								*/
	{CALT,			1,16,16,L0|L1}, /* 83: 100x xxxx								*/
	{CALT,			1,16,16,L0|L1}, /* 84: 100x xxxx								*/
	{CALT,			1,16,16,L0|L1}, /* 85: 100x xxxx								*/
	{CALT,			1,16,16,L0|L1}, /* 86: 100x xxxx								*/
	{CALT,			1,16,16,L0|L1}, /* 87: 100x xxxx								*/
	{CALT,			1,16,16,L0|L1}, /* 88: 100x xxxx								*/
	{CALT,			1,16,16,L0|L1}, /* 89: 100x xxxx								*/
	{CALT,			1,16,16,L0|L1}, /* 8a: 100x xxxx								*/
	{CALT,			1,16,16,L0|L1}, /* 8b: 100x xxxx								*/
	{CALT,			1,16,16,L0|L1}, /* 8c: 100x xxxx								*/
	{CALT,			1,16,16,L0|L1}, /* 8d: 100x xxxx								*/
	{CALT,			1,16,16,L0|L1}, /* 8e: 100x xxxx								*/
	{CALT,			1,16,16,L0|L1}, /* 8f: 100x xxxx								*/

	{CALT,			1,16,16,L0|L1}, /* 90: 100x xxxx								*/
	{CALT,			1,16,16,L0|L1}, /* 91: 100x xxxx								*/
	{CALT,			1,16,16,L0|L1}, /* 92: 100x xxxx								*/
	{CALT,			1,16,16,L0|L1}, /* 93: 100x xxxx								*/
	{CALT,			1,16,16,L0|L1}, /* 94: 100x xxxx								*/
	{CALT,			1,16,16,L0|L1}, /* 95: 100x xxxx								*/
	{CALT,			1,16,16,L0|L1}, /* 96: 100x xxxx								*/
	{CALT,			1,16,16,L0|L1}, /* 97: 100x xxxx								*/
	{CALT,			1,16,16,L0|L1}, /* 98: 100x xxxx								*/
	{CALT,			1,16,16,L0|L1}, /* 99: 100x xxxx								*/
	{CALT,			1,16,16,L0|L1}, /* 9a: 100x xxxx								*/
	{CALT,			1,16,16,L0|L1}, /* 9b: 100x xxxx								*/
	{CALT,			1,16,16,L0|L1}, /* 9c: 100x xxxx								*/
	{CALT,			1,16,16,L0|L1}, /* 9d: 100x xxxx								*/
	{CALT,			1,16,16,L0|L1}, /* 9e: 100x xxxx								*/
	{CALT,			1,16,16,L0|L1}, /* 9f: 100x xxxx								*/

	{POP_VA,		1,10,10,L0|L1}, /* a0: 1010 0000								*/
	{POP_BC,		1,10,10,L0|L1}, /* a1: 1010 0001								*/
	{POP_DE,		1,10,10,L0|L1}, /* a2: 1010 0010								*/
	{POP_HL,		1,10,10,L0|L1}, /* a3: 1010 0011								*/
	{POP_EA,		1,10,10,L0|L1}, /* a4: 1010 0100								*/
	{DMOV_EA_BC,	1, 4, 4,L0|L1}, /* a5: 1010 0101								*/
	{DMOV_EA_DE,	1, 4, 4,L0|L1}, /* a6: 1010 0110								*/
	{DMOV_EA_HL,	1, 4, 4,L0|L1}, /* a7: 1010 0111								*/
	{INX_EA,		1, 7, 7,L0|L1}, /* a8: 1010 1000								*/
	{DCX_EA,		1, 7, 7,L0|L1}, /* a9: 1010 1001								*/
	{EI,			1, 4, 4,L0|L1}, /* aa: 1010 1010								*/
	{LDAX_D_xx, 	2, 7, 7,L0|L1}, /* ab: 1010 1011 dddd dddd						*/
	{LDAX_H_A,		1, 7, 7,L0|L1}, /* ac: 1010 1100								*/
	{LDAX_H_B,		1, 7, 7,L0|L1}, /* ad: 1010 1101								*/
	{LDAX_H_EA, 	1, 7, 7,L0|L1}, /* ae: 1010 1110								*/
	{LDAX_H_xx, 	2, 7, 7,L0|L1}, /* af: 1010 1111 dddd dddd						*/

	{PUSH_VA,		1,13,13,L0|L1}, /* b0: 1011 0000								*/
	{PUSH_BC,		1,13,13,L0|L1}, /* b1: 1011 0001								*/
	{PUSH_DE,		1,13,13,L0|L1}, /* b2: 1011 0010								*/
	{PUSH_HL,		1,13,13,L0|L1}, /* b3: 1011 0011								*/
	{PUSH_EA,		1,13,13,L0|L1}, /* b4: 1011 0100								*/
	{DMOV_BC_EA,	1, 4, 4,L0|L1}, /* b5: 1011 0101								*/
	{DMOV_DE_EA,	1, 4, 4,L0|L1}, /* b6: 1011 0110								*/
	{DMOV_HL_EA,	1, 4, 4,L0|L1}, /* b7: 1011 0111								*/
	{RET,			1,10,10,L0|L1}, /* b8: 1011 1000								*/
	{RETS,			1,10,10,L0|L1}, /* b9: 1011 1001								*/
	{DI,			1, 4, 4,L0|L1}, /* ba: 1011 1010								*/
	{STAX_D_xx, 	2, 7, 7,L0|L1}, /* bb: 1011 1011 dddd dddd						*/
	{STAX_H_A,		1, 7, 7,L0|L1}, /* bc: 1011 1100								*/
	{STAX_H_B,		1, 7, 7,L0|L1}, /* bd: 1011 1101								*/
	{STAX_H_EA, 	1, 7, 7,L0|L1}, /* be: 1011 1110								*/
	{STAX_H_xx, 	2, 7, 7,L0|L1}, /* bf: 1011 1111 dddd dddd						*/

	{JR,			1,10,10,L0|L1}, /* c0: 1100 0000								*/
	{JR,			1,10,10,L0|L1}, /* c1: 1100 0001								*/
	{JR,			1,10,10,L0|L1}, /* c2: 1100 0010								*/
	{JR,			1,10,10,L0|L1}, /* c3: 1100 0011								*/
	{JR,			1,10,10,L0|L1}, /* c4: 1100 0100								*/
	{JR,			1,10,10,L0|L1}, /* c5: 1100 0101								*/
	{JR,			1,10,10,L0|L1}, /* c6: 1100 0110								*/
	{JR,			1,10,10,L0|L1}, /* c7: 1100 0111								*/
	{JR,			1,10,10,L0|L1}, /* c8: 1100 1000								*/
	{JR,			1,10,10,L0|L1}, /* c9: 1100 1001								*/
	{JR,			1,10,10,L0|L1}, /* ca: 1100 1010								*/
	{JR,			1,10,10,L0|L1}, /* cb: 1100 1011								*/
	{JR,			1,10,10,L0|L1}, /* cc: 1100 1100								*/
	{JR,			1,10,10,L0|L1}, /* cd: 1100 1101								*/
	{JR,			1,10,10,L0|L1}, /* ce: 1100 1110								*/
	{JR,			1,10,10,L0|L1}, /* cf: 1100 1111								*/

	{JR,			1,10,10,L0|L1}, /* d0: 1101 0000								*/
	{JR,			1,10,10,L0|L1}, /* d1: 1101 0001								*/
	{JR,			1,10,10,L0|L1}, /* d2: 1101 0010								*/
	{JR,			1,10,10,L0|L1}, /* d3: 1101 0011								*/
	{JR,			1,10,10,L0|L1}, /* d4: 1101 0100								*/
	{JR,			1,10,10,L0|L1}, /* d5: 1101 0101								*/
	{JR,			1,10,10,L0|L1}, /* d6: 1101 0110								*/
	{JR,			1,10,10,L0|L1}, /* d7: 1101 0111								*/
	{JR,			1,10,10,L0|L1}, /* d8: 1101 1000								*/
	{JR,			1,10,10,L0|L1}, /* d9: 1101 1001								*/
	{JR,			1,10,10,L0|L1}, /* da: 1101 1010								*/
	{JR,			1,10,10,L0|L1}, /* db: 1101 1011								*/
	{JR,			1,10,10,L0|L1}, /* dc: 1101 1100								*/
	{JR,			1,10,10,L0|L1}, /* dd: 1101 1101								*/
	{JR,			1,10,10,L0|L1}, /* de: 1101 1110								*/
	{JR,			1,10,10,L0|L1}, /* df: 1101 1111								*/

	{JR,			1,10,10,L0|L1}, /* e0: 1110 0000								*/
	{JR,			1,10,10,L0|L1}, /* e1: 1110 0001								*/
	{JR,			1,10,10,L0|L1}, /* e2: 1110 0010								*/
	{JR,			1,10,10,L0|L1}, /* e3: 1110 0011								*/
	{JR,			1,10,10,L0|L1}, /* e4: 1110 0100								*/
	{JR,			1,10,10,L0|L1}, /* e5: 1110 0101								*/
	{JR,			1,10,10,L0|L1}, /* e6: 1110 0110								*/
	{JR,			1,10,10,L0|L1}, /* e7: 1110 0111								*/
	{JR,			1,10,10,L0|L1}, /* e8: 1110 1000								*/
	{JR,			1,10,10,L0|L1}, /* e9: 1110 1001								*/
	{JR,			1,10,10,L0|L1}, /* ea: 1110 1010								*/
	{JR,			1,10,10,L0|L1}, /* eb: 1110 1011								*/
	{JR,			1,10,10,L0|L1}, /* ec: 1110 1100								*/
	{JR,			1,10,10,L0|L1}, /* ed: 1110 1101								*/
	{JR,			1,10,10,L0|L1}, /* ee: 1110 1110								*/
	{JR,			1,10,10,L0|L1}, /* ef: 1110 1111								*/

	{JR,			1,10,10,L0|L1}, /* f0: 1111 0000								*/
	{JR,			1,10,10,L0|L1}, /* f1: 1111 0001								*/
	{JR,			1,10,10,L0|L1}, /* f2: 1111 0010								*/
	{JR,			1,10,10,L0|L1}, /* f3: 1111 0011								*/
	{JR,			1,10,10,L0|L1}, /* f4: 1111 0100								*/
	{JR,			1,10,10,L0|L1}, /* f5: 1111 0101								*/
	{JR,			1,10,10,L0|L1}, /* f6: 1111 0110								*/
	{JR,			1,10,10,L0|L1}, /* f7: 1111 0111								*/
	{JR,			1,10,10,L0|L1}, /* f8: 1111 1000								*/
	{JR,			1,10,10,L0|L1}, /* f9: 1111 1001								*/
	{JR,			1,10,10,L0|L1}, /* fa: 1111 1010								*/
	{JR,			1,10,10,L0|L1}, /* fb: 1111 1011								*/
	{JR,			1,10,10,L0|L1}, /* fc: 1111 1100								*/
	{JR,			1,10,10,L0|L1}, /* fd: 1111 1101								*/
	{JR,			1,10,10,L0|L1}, /* fe: 1111 1110								*/
	{JR,			1,10,10,L0|L1}	/* ff: 1111 1111								*/
};

