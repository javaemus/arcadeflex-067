package gr.codebb.arcadeflex.v067.mame;

import static gr.codebb.arcadeflex.common.PtrLib.*;
import static gr.codebb.arcadeflex.v067.common.FuncPtr.*;

public class memoryH {
//TODO /***************************************************************************
//TODO 
//TODO 	Basic type definitions
//TODO 
//TODO 	These types are used for memory handlers.
//TODO 
//TODO ***************************************************************************/
//TODO 
//TODO /* ----- typedefs for data and offset types ----- */
//TODO typedef UINT8			data8_t;
//TODO typedef UINT16			data16_t;
//TODO typedef UINT32			data32_t;
//TODO typedef UINT32			offs_t;
//TODO 
//TODO /* ----- typedefs for the various common memory/port handlers ----- */
//TODO typedef data8_t			(*read8_handler)  (UNUSEDARG offs_t offset);
//TODO typedef void			(*write8_handler) (UNUSEDARG offs_t offset, UNUSEDARG data8_t data);
//TODO typedef data16_t		(*read16_handler) (UNUSEDARG offs_t offset, UNUSEDARG data16_t mem_mask);
//TODO typedef void			(*write16_handler)(UNUSEDARG offs_t offset, UNUSEDARG data16_t data, UNUSEDARG data16_t mem_mask);
//TODO typedef data32_t		(*read32_handler) (UNUSEDARG offs_t offset, UNUSEDARG data32_t mem_mask);
//TODO typedef void			(*write32_handler)(UNUSEDARG offs_t offset, UNUSEDARG data32_t data, UNUSEDARG data32_t mem_mask);
//TODO typedef offs_t			(*opbase_handler) (UNUSEDARG offs_t address);
//TODO 
//TODO /* ----- typedefs for the various common memory handlers ----- */
//TODO typedef read8_handler	mem_read_handler;
//TODO typedef write8_handler	mem_write_handler;
//TODO typedef read16_handler	mem_read16_handler;
//TODO typedef write16_handler	mem_write16_handler;
//TODO typedef read32_handler	mem_read32_handler;
//TODO typedef write32_handler	mem_write32_handler;
//TODO 
//TODO /* ----- typedefs for the various common port handlers ----- */
//TODO typedef read8_handler	port_read_handler;
//TODO typedef write8_handler	port_write_handler;
//TODO typedef read16_handler	port_read16_handler;
//TODO typedef write16_handler	port_write16_handler;
//TODO typedef read32_handler	port_read32_handler;
//TODO typedef write32_handler	port_write32_handler;
//TODO 
//TODO /* ----- typedefs for externally allocated memory ----- */
//TODO struct ExtMemory
//TODO {
//TODO 	offs_t 			start, end;
//TODO 	UINT8			region;
//TODO     UINT8 *			data;
//TODO };
//TODO 
//TODO 
//TODO 
//TODO /***************************************************************************
//TODO 
//TODO 	Basic macros
//TODO 
//TODO ***************************************************************************/
//TODO 
//TODO /* ----- macros for declaring the various common memory/port handlers ----- */
//TODO #define READ_HANDLER(name) 		data8_t  name(UNUSEDARG offs_t offset)
//TODO #define WRITE_HANDLER(name) 	void     name(UNUSEDARG offs_t offset, UNUSEDARG data8_t data)
//TODO #define READ16_HANDLER(name)	data16_t name(UNUSEDARG offs_t offset, UNUSEDARG data16_t mem_mask)
//TODO #define WRITE16_HANDLER(name)	void     name(UNUSEDARG offs_t offset, UNUSEDARG data16_t data, UNUSEDARG data16_t mem_mask)
//TODO #define READ32_HANDLER(name)	data32_t name(UNUSEDARG offs_t offset, UNUSEDARG data32_t mem_mask)
//TODO #define WRITE32_HANDLER(name)	void     name(UNUSEDARG offs_t offset, UNUSEDARG data32_t data, UNUSEDARG data32_t mem_mask)
//TODO #define OPBASE_HANDLER(name)	offs_t   name(UNUSEDARG offs_t address)
//TODO 
//TODO /* ----- macros for accessing bytes and words within larger chunks ----- */
//TODO #ifdef LSB_FIRST
//TODO 	#define BYTE_XOR_BE(a)  	((a) ^ 1)				/* read/write a byte to a 16-bit space */
//TODO 	#define BYTE_XOR_LE(a)  	(a)
//TODO 	#define BYTE4_XOR_BE(a) 	((a) ^ 3)				/* read/write a byte to a 32-bit space */
//TODO 	#define BYTE4_XOR_LE(a) 	(a)
//TODO 	#define WORD_XOR_BE(a)  	((a) ^ 2)				/* read/write a word to a 32-bit space */
//TODO 	#define WORD_XOR_LE(a)  	(a)
//TODO #else
//TODO 	#define BYTE_XOR_BE(a)  	(a)
//TODO 	#define BYTE_XOR_LE(a)  	((a) ^ 1)				/* read/write a byte to a 16-bit space */
//TODO 	#define BYTE4_XOR_BE(a) 	(a)
//TODO 	#define BYTE4_XOR_LE(a) 	((a) ^ 3)				/* read/write a byte to a 32-bit space */
//TODO 	#define WORD_XOR_BE(a)  	(a)
//TODO 	#define WORD_XOR_LE(a)  	((a) ^ 2)				/* read/write a word to a 32-bit space */
//TODO #endif
//TODO 
//TODO 
//TODO 

    /**
     * *************************************************************************
     *
     * Memory/port array constants
     *
     * These apply to values in the array of read/write handlers that is
     * declared within each driver.
     *
     **************************************************************************
     */

    /* ----- memory/port width constants ----- */
    public static final int MEMPORT_WIDTH_MASK = 0x00000003;/* mask to get at the width bits */
    public static final int MEMPORT_WIDTH_8 = 0x00000001;/* this memory/port array is for an 8-bit databus */
    public static final int MEMPORT_WIDTH_16 = 0x00000002;/* this memory/port array is for a 16-bit databus */
    public static final int MEMPORT_WIDTH_32 = 0x00000003;/* this memory/port array is for a 32-bit databus */

 /* ----- memory/port type constants ----- */
    public static final int MEMPORT_TYPE_MASK = 0x30000000;/* mask to get at the type bits */
    public static final int MEMPORT_TYPE_MEM = 0x10000000;/* this memory/port array is for memory */
    public static final int MEMPORT_TYPE_IO = 0x20000000;/* this memory/port array is for ports */

 /* ----- memory/port direction constants ----- */
    public static final int MEMPORT_DIRECTION_MASK = 0xc0000000;/* mask to get at the direction bits */
    public static final int MEMPORT_DIRECTION_READ = 0x40000000;/* this memory/port array is for reads */
    public static final int MEMPORT_DIRECTION_WRITE = 0x80000000;/* this memory/port array is for writes */

 /* ----- memory/port address bits constants ----- */
    public static final int MEMPORT_ABITS_MASK = 0x08000000;/* set this bit to indicate the entry has address bits */
    public static final int MEMPORT_ABITS_VAL_MASK = 0x000000ff;/* number of address bits */

 /* ----- memory/port struct marker constants ----- */
    public static final int MEMPORT_MARKER = Integer.MAX_VALUE;/*((offs_t)~0);*//* used in the end field to indicate end of array */
//TODO 
//TODO /* ----- static memory/port handler constants ----- */
//TODO #define STATIC_INVALID			0						/* invalid - should never be used */
//TODO #define STATIC_BANK1			1						/* banked memory #1 */
//TODO #define STATIC_BANK2			2						/* banked memory #2 */
//TODO #define STATIC_BANK3			3						/* banked memory #3 */
//TODO #define STATIC_BANK4			4						/* banked memory #4 */
//TODO #define STATIC_BANK5			5						/* banked memory #5 */
//TODO #define STATIC_BANK6			6						/* banked memory #6 */
//TODO #define STATIC_BANK7			7						/* banked memory #7 */
//TODO #define STATIC_BANK8			8						/* banked memory #8 */
//TODO #define STATIC_BANK9			9						/* banked memory #9 */
//TODO #define STATIC_BANK10			10						/* banked memory #10 */
//TODO #define STATIC_BANK11			11						/* banked memory #11 */
//TODO #define STATIC_BANK12			12						/* banked memory #12 */
//TODO #define STATIC_BANK13			13						/* banked memory #13 */
//TODO #define STATIC_BANK14			14						/* banked memory #14 */
//TODO #define STATIC_BANK15			15						/* banked memory #15 */
//TODO #define STATIC_BANK16			16						/* banked memory #16 */
//TODO #define STATIC_BANK17			17						/* banked memory #17 */
//TODO #define STATIC_BANK18			18						/* banked memory #18 */
//TODO #define STATIC_BANK19			19						/* banked memory #19 */
//TODO #define STATIC_BANK20			20						/* banked memory #20 */
//TODO #define STATIC_BANK21			21						/* banked memory #21 */
//TODO #define STATIC_BANK22			22						/* banked memory #22 */
//TODO #define STATIC_BANK23			23						/* banked memory #23 */
//TODO #define STATIC_BANK24			24						/* banked memory #24 */
    public static final int STATIC_RAM = 25;/* RAM - standard reads/writes */
    public static final int STATIC_ROM = 26;/* ROM - just like RAM, but writes to the bit-bucket */
//TODO #define STATIC_RAMROM			27						/* RAMROM - use for access in encrypted 8-bit systems */
    public static final int STATIC_NOP = 28;/* NOP - reads are 0, writes to the bit-bucket */
//TODO #define STATIC_UNUSED1			29						/* unused - reserved for future use */
//TODO #define STATIC_UNUSED2			30						/* unused - reserved for future use */
//TODO #define STATIC_UNMAP			31						/* unmapped - all unmapped memory goes here */
//TODO #define STATIC_COUNT			32						/* total number of static handlers */
//TODO 
//TODO /* ----- banking constants ----- */
//TODO #define MAX_BANKS				24						/* maximum number of banks */
//TODO #define STATIC_BANKMAX			(STATIC_RAM - 1)		/* handler constant of last bank */
//TODO 
//TODO 
//TODO 
//TODO /***************************************************************************
//TODO 
//TODO 	Constants for static entries in memory read/write arrays
//TODO 
//TODO 	The first 32 entries in the memory lookup table are reserved for
//TODO 	"static" handlers. These are internal handlers for RAM, ROM, banks,
//TODO 	and unmapped memory areas. The following definitions are the
//TODO 	properly-casted versions of the STATIC_ constants above.
//TODO 
//TODO ***************************************************************************/
//TODO 
//TODO /* 8-bit reads */
//TODO #define MRA_BANK1				((mem_read_handler)STATIC_BANK1)
//TODO #define MRA_BANK2				((mem_read_handler)STATIC_BANK2)
//TODO #define MRA_BANK3				((mem_read_handler)STATIC_BANK3)
//TODO #define MRA_BANK4				((mem_read_handler)STATIC_BANK4)
//TODO #define MRA_BANK5				((mem_read_handler)STATIC_BANK5)
//TODO #define MRA_BANK6				((mem_read_handler)STATIC_BANK6)
//TODO #define MRA_BANK7				((mem_read_handler)STATIC_BANK7)
//TODO #define MRA_BANK8				((mem_read_handler)STATIC_BANK8)
//TODO #define MRA_BANK9				((mem_read_handler)STATIC_BANK9)
//TODO #define MRA_BANK10				((mem_read_handler)STATIC_BANK10)
//TODO #define MRA_BANK11				((mem_read_handler)STATIC_BANK11)
//TODO #define MRA_BANK12				((mem_read_handler)STATIC_BANK12)
//TODO #define MRA_BANK13				((mem_read_handler)STATIC_BANK13)
//TODO #define MRA_BANK14				((mem_read_handler)STATIC_BANK14)
//TODO #define MRA_BANK15				((mem_read_handler)STATIC_BANK15)
//TODO #define MRA_BANK16				((mem_read_handler)STATIC_BANK16)
//TODO #define MRA_BANK17				((mem_read_handler)STATIC_BANK17)
//TODO #define MRA_BANK18				((mem_read_handler)STATIC_BANK18)
//TODO #define MRA_BANK19				((mem_read_handler)STATIC_BANK19)
//TODO #define MRA_BANK20				((mem_read_handler)STATIC_BANK20)
//TODO #define MRA_BANK21				((mem_read_handler)STATIC_BANK21)
//TODO #define MRA_BANK22				((mem_read_handler)STATIC_BANK22)
//TODO #define MRA_BANK23				((mem_read_handler)STATIC_BANK23)
//TODO #define MRA_BANK24				((mem_read_handler)STATIC_BANK24)
//TODO #define MRA_NOP					((mem_read_handler)STATIC_NOP)
    public static final int MRA_RAM = STATIC_RAM;
    public static final int MRA_ROM = STATIC_ROM;
//TODO #define MRA_RAMROM				((mem_read_handler)STATIC_RAMROM)
//TODO 
//TODO /* 8-bit writes */
//TODO #define MWA_BANK1				((mem_write_handler)STATIC_BANK1)
//TODO #define MWA_BANK2				((mem_write_handler)STATIC_BANK2)
//TODO #define MWA_BANK3				((mem_write_handler)STATIC_BANK3)
//TODO #define MWA_BANK4				((mem_write_handler)STATIC_BANK4)
//TODO #define MWA_BANK5				((mem_write_handler)STATIC_BANK5)
//TODO #define MWA_BANK6				((mem_write_handler)STATIC_BANK6)
//TODO #define MWA_BANK7				((mem_write_handler)STATIC_BANK7)
//TODO #define MWA_BANK8				((mem_write_handler)STATIC_BANK8)
//TODO #define MWA_BANK9				((mem_write_handler)STATIC_BANK9)
//TODO #define MWA_BANK10				((mem_write_handler)STATIC_BANK10)
//TODO #define MWA_BANK11				((mem_write_handler)STATIC_BANK11)
//TODO #define MWA_BANK12				((mem_write_handler)STATIC_BANK12)
//TODO #define MWA_BANK13				((mem_write_handler)STATIC_BANK13)
//TODO #define MWA_BANK14				((mem_write_handler)STATIC_BANK14)
//TODO #define MWA_BANK15				((mem_write_handler)STATIC_BANK15)
//TODO #define MWA_BANK16				((mem_write_handler)STATIC_BANK16)
//TODO #define MWA_BANK17				((mem_write_handler)STATIC_BANK17)
//TODO #define MWA_BANK18				((mem_write_handler)STATIC_BANK18)
//TODO #define MWA_BANK19				((mem_write_handler)STATIC_BANK19)
//TODO #define MWA_BANK20				((mem_write_handler)STATIC_BANK20)
//TODO #define MWA_BANK21				((mem_write_handler)STATIC_BANK21)
//TODO #define MWA_BANK22				((mem_write_handler)STATIC_BANK22)
//TODO #define MWA_BANK23				((mem_write_handler)STATIC_BANK23)
//TODO #define MWA_BANK24				((mem_write_handler)STATIC_BANK24)
    public static final int MWA_NOP = STATIC_NOP;
//TODO #define MWA_RAM					((mem_write_handler)STATIC_RAM)
    public static final int MWA_ROM = STATIC_ROM;
//TODO #define MWA_RAMROM				((mem_write_handler)STATIC_RAMROM)
//TODO 
//TODO /* 16-bit reads */
//TODO #define MRA16_BANK1				((mem_read16_handler)STATIC_BANK1)
//TODO #define MRA16_BANK2				((mem_read16_handler)STATIC_BANK2)
//TODO #define MRA16_BANK3				((mem_read16_handler)STATIC_BANK3)
//TODO #define MRA16_BANK4				((mem_read16_handler)STATIC_BANK4)
//TODO #define MRA16_BANK5				((mem_read16_handler)STATIC_BANK5)
//TODO #define MRA16_BANK6				((mem_read16_handler)STATIC_BANK6)
//TODO #define MRA16_BANK7				((mem_read16_handler)STATIC_BANK7)
//TODO #define MRA16_BANK8				((mem_read16_handler)STATIC_BANK8)
//TODO #define MRA16_BANK9				((mem_read16_handler)STATIC_BANK9)
//TODO #define MRA16_BANK10			((mem_read16_handler)STATIC_BANK10)
//TODO #define MRA16_BANK11			((mem_read16_handler)STATIC_BANK11)
//TODO #define MRA16_BANK12			((mem_read16_handler)STATIC_BANK12)
//TODO #define MRA16_BANK13			((mem_read16_handler)STATIC_BANK13)
//TODO #define MRA16_BANK14			((mem_read16_handler)STATIC_BANK14)
//TODO #define MRA16_BANK15			((mem_read16_handler)STATIC_BANK15)
//TODO #define MRA16_BANK16			((mem_read16_handler)STATIC_BANK16)
//TODO #define MRA16_BANK17			((mem_read16_handler)STATIC_BANK17)
//TODO #define MRA16_BANK18			((mem_read16_handler)STATIC_BANK18)
//TODO #define MRA16_BANK19			((mem_read16_handler)STATIC_BANK19)
//TODO #define MRA16_BANK20			((mem_read16_handler)STATIC_BANK20)
//TODO #define MRA16_BANK21			((mem_read16_handler)STATIC_BANK21)
//TODO #define MRA16_BANK22			((mem_read16_handler)STATIC_BANK22)
//TODO #define MRA16_BANK23			((mem_read16_handler)STATIC_BANK23)
//TODO #define MRA16_BANK24			((mem_read16_handler)STATIC_BANK24)
//TODO #define MRA16_NOP				((mem_read16_handler)STATIC_NOP)
//TODO #define MRA16_RAM				((mem_read16_handler)STATIC_RAM)
//TODO #define MRA16_ROM				((mem_read16_handler)STATIC_ROM)
//TODO 
//TODO /* 16-bit writes */
//TODO #define MWA16_BANK1				((mem_write16_handler)STATIC_BANK1)
//TODO #define MWA16_BANK2				((mem_write16_handler)STATIC_BANK2)
//TODO #define MWA16_BANK3				((mem_write16_handler)STATIC_BANK3)
//TODO #define MWA16_BANK4				((mem_write16_handler)STATIC_BANK4)
//TODO #define MWA16_BANK5				((mem_write16_handler)STATIC_BANK5)
//TODO #define MWA16_BANK6				((mem_write16_handler)STATIC_BANK6)
//TODO #define MWA16_BANK7				((mem_write16_handler)STATIC_BANK7)
//TODO #define MWA16_BANK8				((mem_write16_handler)STATIC_BANK8)
//TODO #define MWA16_BANK9				((mem_write16_handler)STATIC_BANK9)
//TODO #define MWA16_BANK10			((mem_write16_handler)STATIC_BANK10)
//TODO #define MWA16_BANK11			((mem_write16_handler)STATIC_BANK11)
//TODO #define MWA16_BANK12			((mem_write16_handler)STATIC_BANK12)
//TODO #define MWA16_BANK13			((mem_write16_handler)STATIC_BANK13)
//TODO #define MWA16_BANK14			((mem_write16_handler)STATIC_BANK14)
//TODO #define MWA16_BANK15			((mem_write16_handler)STATIC_BANK15)
//TODO #define MWA16_BANK16			((mem_write16_handler)STATIC_BANK16)
//TODO #define MWA16_BANK17			((mem_write16_handler)STATIC_BANK17)
//TODO #define MWA16_BANK18			((mem_write16_handler)STATIC_BANK18)
//TODO #define MWA16_BANK19			((mem_write16_handler)STATIC_BANK19)
//TODO #define MWA16_BANK20			((mem_write16_handler)STATIC_BANK20)
//TODO #define MWA16_BANK21			((mem_write16_handler)STATIC_BANK21)
//TODO #define MWA16_BANK22			((mem_write16_handler)STATIC_BANK22)
//TODO #define MWA16_BANK23			((mem_write16_handler)STATIC_BANK23)
//TODO #define MWA16_BANK24			((mem_write16_handler)STATIC_BANK24)
//TODO #define MWA16_NOP				((mem_write16_handler)STATIC_NOP)
//TODO #define MWA16_RAM				((mem_write16_handler)STATIC_RAM)
//TODO #define MWA16_ROM				((mem_write16_handler)STATIC_ROM)
//TODO 
//TODO /* 32-bit reads */
//TODO #define MRA32_BANK1				((mem_read32_handler)STATIC_BANK1)
//TODO #define MRA32_BANK2				((mem_read32_handler)STATIC_BANK2)
//TODO #define MRA32_BANK3				((mem_read32_handler)STATIC_BANK3)
//TODO #define MRA32_BANK4				((mem_read32_handler)STATIC_BANK4)
//TODO #define MRA32_BANK5				((mem_read32_handler)STATIC_BANK5)
//TODO #define MRA32_BANK6				((mem_read32_handler)STATIC_BANK6)
//TODO #define MRA32_BANK7				((mem_read32_handler)STATIC_BANK7)
//TODO #define MRA32_BANK8				((mem_read32_handler)STATIC_BANK8)
//TODO #define MRA32_BANK9				((mem_read32_handler)STATIC_BANK9)
//TODO #define MRA32_BANK10			((mem_read32_handler)STATIC_BANK10)
//TODO #define MRA32_BANK11			((mem_read32_handler)STATIC_BANK11)
//TODO #define MRA32_BANK12			((mem_read32_handler)STATIC_BANK12)
//TODO #define MRA32_BANK13			((mem_read32_handler)STATIC_BANK13)
//TODO #define MRA32_BANK14			((mem_read32_handler)STATIC_BANK14)
//TODO #define MRA32_BANK15			((mem_read32_handler)STATIC_BANK15)
//TODO #define MRA32_BANK16			((mem_read32_handler)STATIC_BANK16)
//TODO #define MRA32_BANK17			((mem_read32_handler)STATIC_BANK17)
//TODO #define MRA32_BANK18			((mem_read32_handler)STATIC_BANK18)
//TODO #define MRA32_BANK19			((mem_read32_handler)STATIC_BANK19)
//TODO #define MRA32_BANK20			((mem_read32_handler)STATIC_BANK20)
//TODO #define MRA32_BANK21			((mem_read32_handler)STATIC_BANK21)
//TODO #define MRA32_BANK22			((mem_read32_handler)STATIC_BANK22)
//TODO #define MRA32_BANK23			((mem_read32_handler)STATIC_BANK23)
//TODO #define MRA32_BANK24			((mem_read32_handler)STATIC_BANK24)
//TODO #define MRA32_NOP				((mem_read32_handler)STATIC_NOP)
//TODO #define MRA32_RAM				((mem_read32_handler)STATIC_RAM)
//TODO #define MRA32_ROM				((mem_read32_handler)STATIC_ROM)
//TODO 
//TODO /* 32-bit writes */
//TODO #define MWA32_BANK1				((mem_write32_handler)STATIC_BANK1)
//TODO #define MWA32_BANK2				((mem_write32_handler)STATIC_BANK2)
//TODO #define MWA32_BANK3				((mem_write32_handler)STATIC_BANK3)
//TODO #define MWA32_BANK4				((mem_write32_handler)STATIC_BANK4)
//TODO #define MWA32_BANK5				((mem_write32_handler)STATIC_BANK5)
//TODO #define MWA32_BANK6				((mem_write32_handler)STATIC_BANK6)
//TODO #define MWA32_BANK7				((mem_write32_handler)STATIC_BANK7)
//TODO #define MWA32_BANK8				((mem_write32_handler)STATIC_BANK8)
//TODO #define MWA32_BANK9				((mem_write32_handler)STATIC_BANK9)
//TODO #define MWA32_BANK10			((mem_write32_handler)STATIC_BANK10)
//TODO #define MWA32_BANK11			((mem_write32_handler)STATIC_BANK11)
//TODO #define MWA32_BANK12			((mem_write32_handler)STATIC_BANK12)
//TODO #define MWA32_BANK13			((mem_write32_handler)STATIC_BANK13)
//TODO #define MWA32_BANK14			((mem_write32_handler)STATIC_BANK14)
//TODO #define MWA32_BANK15			((mem_write32_handler)STATIC_BANK15)
//TODO #define MWA32_BANK16			((mem_write32_handler)STATIC_BANK16)
//TODO #define MWA32_BANK17			((mem_write32_handler)STATIC_BANK17)
//TODO #define MWA32_BANK18			((mem_write32_handler)STATIC_BANK18)
//TODO #define MWA32_BANK19			((mem_write32_handler)STATIC_BANK19)
//TODO #define MWA32_BANK20			((mem_write32_handler)STATIC_BANK20)
//TODO #define MWA32_BANK21			((mem_write32_handler)STATIC_BANK21)
//TODO #define MWA32_BANK22			((mem_write32_handler)STATIC_BANK22)
//TODO #define MWA32_BANK23			((mem_write32_handler)STATIC_BANK23)
//TODO #define MWA32_BANK24			((mem_write32_handler)STATIC_BANK24)
//TODO #define MWA32_NOP				((mem_write32_handler)STATIC_NOP)
//TODO #define MWA32_RAM				((mem_write32_handler)STATIC_RAM)
//TODO #define MWA32_ROM				((mem_write32_handler)STATIC_ROM)
//TODO 
//TODO 
//TODO 
//TODO /***************************************************************************
//TODO 
//TODO 	Constants for static entries in port read/write arrays
//TODO 
//TODO ***************************************************************************/
//TODO 
//TODO /* 8-bit port reads */
//TODO #define IORP_NOP				((port_read_handler)STATIC_NOP)
//TODO 
//TODO /* 8-bit port writes */
//TODO #define IOWP_NOP				((port_write_handler)STATIC_NOP)
//TODO 
//TODO /* 16-bit port reads */
//TODO #define IORP16_NOP				((port_read16_handler)STATIC_NOP)
//TODO 
//TODO /* 16-bit port writes */
//TODO #define IOWP16_NOP				((port_write16_handler)STATIC_NOP)
//TODO 
//TODO /* 32-bit port reads */
//TODO #define IORP32_NOP				((port_read32_handler)STATIC_NOP)
//TODO 
//TODO /* 32-bit port writes */
//TODO #define IOWP32_NOP				((port_write32_handler)STATIC_NOP)
//TODO 
//TODO 
//TODO 
//TODO /***************************************************************************
//TODO 
//TODO 	Memory/port array type definitions
//TODO 
//TODO 	Note that the memory hooks are not passed the actual memory address
//TODO 	where the operation takes place, but the offset from the beginning
//TODO 	of the block they are assigned to. This makes handling of mirror
//TODO 	addresses easier, and makes the handlers a bit more "object oriented".
//TODO 	If you handler needs to read/write the main memory area, provide a
//TODO 	"base" pointer: it will be initialized by the main engine to point to
//TODO 	the beginning of the memory block assigned to the handler. You may
//TODO 	also provided a pointer to "size": it will be set to the length of
//TODO 	the memory area processed by the handler.
//TODO 
//TODO ***************************************************************************/
//TODO 
/* ----- structs for memory read arrays ----- */
 /* ----- structs for memory read arrays ----- */
    public static class Memory_ReadAddress {

        public Memory_ReadAddress(int start, int end) {
            this.start = start;
            this.end = end;
            this.handler = -15000;//random number for not matching something else
            this._handler = null;
        }

        public Memory_ReadAddress(int start, int end, int handler) {
            this.start = start;
            this.end = end;
            this.handler = handler;
            this._handler = null;
        }

        public Memory_ReadAddress(int start, int end, ReadHandlerPtr _handler) {
            this.start = start;
            this.end = end;
            this.handler = -15000;//random number for not matching something else
            this._handler = _handler;
        }

        int start, end;/* start, end addresses, inclusive */
        ReadHandlerPtr _handler;/* handler callback */
        int handler;
    }
//TODO 
//TODO struct Memory_ReadAddress16
//TODO {
//TODO 	offs_t				start, end;		/* start, end addresses, inclusive */
//TODO 	mem_read16_handler 	handler;		/* handler callback */
//TODO };
//TODO 
//TODO struct Memory_ReadAddress32
//TODO {
//TODO 	offs_t				start, end;		/* start, end addresses, inclusive */
//TODO 	mem_read32_handler	handler;		/* handler callback */
//TODO };
//TODO 
/* ----- structs for memory write arrays ----- */
    public static class Memory_WriteAddress {

        public Memory_WriteAddress(int start, int end) {
            this.start = start;
            this.end = end;
            this._handler = null;
            this.handler = -15000;//random number for not matching something else
            this.base = null;
            this.size = null;
        }

        public Memory_WriteAddress(int start, int end, int handler) {
            this.start = start;
            this.end = end;
            this.handler = handler;
            this._handler = null;
            this.base = null;
            this.size = null;
        }

        public Memory_WriteAddress(int start, int end, int handler, UBytePtr base) {
            this.start = start;
            this.end = end;
            this.handler = handler;
            this._handler = null;
            this.base = base;
            this.size = null;
        }

        public Memory_WriteAddress(int start, int end, int handler, UBytePtr base, int[] size) {
            this.start = start;
            this.end = end;
            this.handler = handler;
            this._handler = null;
            this.base = base;
            this.size = size;
        }

        public Memory_WriteAddress(int start, int end, WriteHandlerPtr _handler) {
            this.start = start;
            this.end = end;
            this.handler = -15000;//random number for not matching something else
            this._handler = _handler;
            this.base = null;
            this.size = null;
        }

        public Memory_WriteAddress(int start, int end, WriteHandlerPtr _handler, UBytePtr base) {
            this.start = start;
            this.end = end;
            this._handler = _handler;
            this.handler = -15000;//random number for not matching something else
            this.base = base;
            this.size = null;
        }

        public Memory_WriteAddress(int start, int end, WriteHandlerPtr _handler, UBytePtr base, int[] size) {
            this.start = start;
            this.end = end;
            this._handler = _handler;
            this.handler = -15000;//random number for not matching something else
            this.base = base;
            this.size = size;
        }

        int start, end;/* start, end addresses, inclusive */
        WriteHandlerPtr _handler;/* handler callback */
        int handler;
        UBytePtr base;/* receives pointer to memory (optional) */
        int[] size;/* receives size of memory in bytes (optional) */
    }
//TODO 
//TODO struct Memory_WriteAddress16
//TODO {
//TODO     offs_t				start, end;		/* start, end addresses, inclusive */
//TODO 	mem_write16_handler handler;		/* handler callback */
//TODO 	data16_t **			base;			/* receives pointer to memory (optional) */
//TODO     size_t *			size;			/* receives size of memory in bytes (optional) */
//TODO };
//TODO 
//TODO struct Memory_WriteAddress32
//TODO {
//TODO     offs_t				start, end;		/* start, end addresses, inclusive */
//TODO 	mem_write32_handler handler;		/* handler callback */
//TODO 	data32_t **			base;			/* receives pointer to memory (optional) */
//TODO 	size_t *			size;			/* receives size of memory in bytes (optional) */
//TODO };
//TODO 
//TODO /* ----- structs for port read arrays ----- */
//TODO struct IO_ReadPort
//TODO {
//TODO 	offs_t				start, end;		/* start, end addresses, inclusive */
//TODO 	port_read_handler 	handler;		/* handler callback */
//TODO };
//TODO 
//TODO struct IO_ReadPort16
//TODO {
//TODO 	offs_t				start, end;		/* start, end addresses, inclusive */
//TODO 	port_read16_handler	handler;		/* handler callback */
//TODO };
//TODO 
//TODO struct IO_ReadPort32
//TODO {
//TODO 	offs_t				start, end;		/* start, end addresses, inclusive */
//TODO 	port_read32_handler	handler;		/* handler callback */
//TODO };
//TODO 
//TODO /* ----- structs for port write arrays ----- */
//TODO struct IO_WritePort
//TODO {
//TODO 	offs_t				start, end;		/* start, end addresses, inclusive */
//TODO 	port_write_handler	handler;		/* handler callback */
//TODO };
//TODO 
//TODO struct IO_WritePort16
//TODO {
//TODO 	offs_t				start, end;		/* start, end addresses, inclusive */
//TODO 	port_write16_handler handler;		/* handler callback */
//TODO };
//TODO 
//TODO struct IO_WritePort32
//TODO {
//TODO 	offs_t				start, end;		/* start, end addresses, inclusive */
//TODO 	port_write32_handler handler;		/* handler callback */
//TODO };
//TODO 
//TODO 
//TODO 
//TODO /***************************************************************************
//TODO 
//TODO 	Memory/port array macros
//TODO 
//TODO ***************************************************************************/
//TODO 
//TODO /* ----- macros for identifying memory/port struct markers ----- */
//TODO #define IS_MEMPORT_MARKER(ma)		((ma)->start == MEMPORT_MARKER && (ma)->end < MEMPORT_MARKER)
//TODO #define IS_MEMPORT_END(ma)			((ma)->start == MEMPORT_MARKER && (ma)->end == 0)
//TODO 
//TODO /* ----- macros for defining the start/stop points ----- */
//TODO #define MEMPORT_ARRAY_START(t,n,f)	const struct t n[] = { { MEMPORT_MARKER, (f) },
//TODO #define MEMPORT_ARRAY_END			{ MEMPORT_MARKER, 0 } };
//TODO 
//TODO /* ----- macros for setting the number of address bits ----- */
//TODO #define MEMPORT_SET_BITS(b)			{ MEMPORT_MARKER, MEMPORT_ABITS_MASK | (b) },
//TODO 
//TODO /* ----- macros for declaring the start of a memory struct array ----- */
//TODO #define MEMORY_READ_START(name)		MEMPORT_ARRAY_START(Memory_ReadAddress,    name, MEMPORT_DIRECTION_READ  | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8)
//TODO #define MEMORY_WRITE_START(name)	MEMPORT_ARRAY_START(Memory_WriteAddress,   name, MEMPORT_DIRECTION_WRITE | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_8)
//TODO #define MEMORY_READ16_START(name)	MEMPORT_ARRAY_START(Memory_ReadAddress16,  name, MEMPORT_DIRECTION_READ  | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_16)
//TODO #define MEMORY_WRITE16_START(name)	MEMPORT_ARRAY_START(Memory_WriteAddress16, name, MEMPORT_DIRECTION_WRITE | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_16)
//TODO #define MEMORY_READ32_START(name)	MEMPORT_ARRAY_START(Memory_ReadAddress32,  name, MEMPORT_DIRECTION_READ  | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_32)
//TODO #define MEMORY_WRITE32_START(name)	MEMPORT_ARRAY_START(Memory_WriteAddress32, name, MEMPORT_DIRECTION_WRITE | MEMPORT_TYPE_MEM | MEMPORT_WIDTH_32)
//TODO 
//TODO #define MEMORY_ADDRESS_BITS(bits)	MEMPORT_SET_BITS(bits)
//TODO #define MEMORY_END					MEMPORT_ARRAY_END
//TODO 
//TODO /* ----- macros for declaring the start of a port struct array ----- */
//TODO #define PORT_READ_START(name)		MEMPORT_ARRAY_START(IO_ReadPort,    name, MEMPORT_DIRECTION_READ  | MEMPORT_TYPE_IO | MEMPORT_WIDTH_8)
//TODO #define PORT_WRITE_START(name)		MEMPORT_ARRAY_START(IO_WritePort,   name, MEMPORT_DIRECTION_WRITE | MEMPORT_TYPE_IO | MEMPORT_WIDTH_8)
//TODO #define PORT_READ16_START(name)		MEMPORT_ARRAY_START(IO_ReadPort16,  name, MEMPORT_DIRECTION_READ  | MEMPORT_TYPE_IO | MEMPORT_WIDTH_16)
//TODO #define PORT_WRITE16_START(name)	MEMPORT_ARRAY_START(IO_WritePort16, name, MEMPORT_DIRECTION_WRITE | MEMPORT_TYPE_IO | MEMPORT_WIDTH_16)
//TODO #define PORT_READ32_START(name)		MEMPORT_ARRAY_START(IO_ReadPort32,  name, MEMPORT_DIRECTION_READ  | MEMPORT_TYPE_IO | MEMPORT_WIDTH_32)
//TODO #define PORT_WRITE32_START(name)	MEMPORT_ARRAY_START(IO_WritePort32, name, MEMPORT_DIRECTION_WRITE | MEMPORT_TYPE_IO | MEMPORT_WIDTH_32)
//TODO 
//TODO #define PORT_ADDRESS_BITS(bits)		MEMPORT_SET_BITS(bits)
//TODO #define PORT_END					MEMPORT_ARRAY_END
//TODO 
//TODO 
//TODO 
//TODO /***************************************************************************
//TODO 
//TODO 	Memory/port lookup constants
//TODO 
//TODO 	These apply to values in the internal lookup table.
//TODO 
//TODO ***************************************************************************/
//TODO 
//TODO /* ----- memory/port lookup table definitions ----- */
//TODO #define SUBTABLE_COUNT			64						/* number of slots reserved for subtables */
//TODO #define SUBTABLE_MASK			(SUBTABLE_COUNT-1)		/* mask to get at the subtable index */
//TODO #define SUBTABLE_BASE			(256-SUBTABLE_COUNT)	/* first index of a subtable */
//TODO #define ENTRY_COUNT				(SUBTABLE_BASE)			/* number of legitimate (non-subtable) entries */
//TODO #define SUBTABLE_ALLOC			8						/* number of subtables to allocate at a time */
//TODO 
//TODO /* ----- bit counts ----- */
//TODO #define LEVEL1_BITS_PREF		12						/* preferred number of bits in the 1st level lookup */
//TODO #define LEVEL1_BITS_BIAS		4						/* number of bits used to bias the L1 bits computation */
//TODO #define SPARSE_THRESH			20						/* number of address bits above which we use sparse memory */
//TODO 
//TODO /* ----- external memory constants ----- */
//TODO #define MAX_EXT_MEMORY			64						/* maximum external memory areas we can allocate */
//TODO 
//TODO 
//TODO 
//TODO /***************************************************************************
//TODO 
//TODO 	Memory/port lookup macros
//TODO 
//TODO 	These are used for accessing the internal lookup table.
//TODO 
//TODO ***************************************************************************/
//TODO 
//TODO /* ----- macros for determining the number of bits to use ----- */
//TODO #define LEVEL1_BITS(x)			(((x) < (2*LEVEL1_BITS_PREF - LEVEL1_BITS_BIAS)) ? LEVEL1_BITS_PREF : ((x) + LEVEL1_BITS_BIAS) / 2)
//TODO #define LEVEL2_BITS(x)			((x) - LEVEL1_BITS(x))
//TODO #define LEVEL1_MASK(x)			((1 << LEVEL1_BITS(x)) - 1)
//TODO #define LEVEL2_MASK(x)			((1 << LEVEL2_BITS(x)) - 1)
//TODO 
//TODO /* ----- table lookup helpers ----- */
//TODO #define LEVEL1_INDEX(a,b,m)		((a) >> (LEVEL2_BITS((b)-(m)) + (m)))
//TODO #define LEVEL2_INDEX(e,a,b,m)	((1 << LEVEL1_BITS((b)-(m))) + (((e) & SUBTABLE_MASK) << LEVEL2_BITS((b)-(m))) + (((a) >> (m)) & LEVEL2_MASK((b)-(m))))
//TODO 
//TODO /* ----- sparse memory space detection ----- */
//TODO #define IS_SPARSE(a)			((a) > SPARSE_THRESH)
//TODO 
//TODO 
//TODO 
//TODO /***************************************************************************
//TODO 
//TODO 	Macros to help declare handlers for core readmem/writemem routines
//TODO 
//TODO ***************************************************************************/
//TODO 
//TODO /* ----- for declaring 8-bit handlers ----- */
//TODO #define DECLARE_HANDLERS_8BIT(type, abits) \
//TODO data8_t  cpu_read##type##abits             (offs_t offset);					\
//TODO void     cpu_write##type##abits            (offs_t offset, data8_t data);
//TODO 
//TODO /* ----- for declaring 16-bit bigendian handlers ----- */
//TODO #define DECLARE_HANDLERS_16BIT_BE(type, abits) \
//TODO data8_t  cpu_read##type##abits##bew        (offs_t offset);					\
//TODO data16_t cpu_read##type##abits##bew_word   (offs_t offset);					\
//TODO void     cpu_write##type##abits##bew       (offs_t offset, data8_t data);	\
//TODO void     cpu_write##type##abits##bew_word  (offs_t offset, data16_t data);
//TODO 
//TODO /* ----- for declaring 16-bit littleendian handlers ----- */
//TODO #define DECLARE_HANDLERS_16BIT_LE(type, abits) \
//TODO data8_t  cpu_read##type##abits##lew        (offs_t offset);					\
//TODO data16_t cpu_read##type##abits##lew_word   (offs_t offset);					\
//TODO void     cpu_write##type##abits##lew       (offs_t offset, data8_t data);	\
//TODO void     cpu_write##type##abits##lew_word  (offs_t offset, data16_t data);
//TODO 
//TODO /* ----- for declaring 32-bit bigendian handlers ----- */
//TODO #define DECLARE_HANDLERS_32BIT_BE(type, abits) \
//TODO data8_t  cpu_read##type##abits##bedw       (offs_t offset);					\
//TODO data16_t cpu_read##type##abits##bedw_word  (offs_t offset);					\
//TODO data32_t cpu_read##type##abits##bedw_dword (offs_t offset);					\
//TODO void     cpu_write##type##abits##bedw      (offs_t offset, data8_t data);	\
//TODO void     cpu_write##type##abits##bedw_word (offs_t offset, data16_t data);	\
//TODO void     cpu_write##type##abits##bedw_dword(offs_t offset, data32_t data);
//TODO 
//TODO /* ----- for declaring 32-bit littleendian handlers ----- */
//TODO #define DECLARE_HANDLERS_32BIT_LE(type, abits) \
//TODO data8_t  cpu_read##type##abits##ledw       (offs_t offset);					\
//TODO data16_t cpu_read##type##abits##ledw_word  (offs_t offset);					\
//TODO data32_t cpu_read##type##abits##ledw_dword (offs_t offset);					\
//TODO void     cpu_write##type##abits##ledw      (offs_t offset, data8_t data);	\
//TODO void     cpu_write##type##abits##ledw_word (offs_t offset, data16_t data);	\
//TODO void     cpu_write##type##abits##ledw_dword(offs_t offset, data32_t data);
//TODO 
//TODO /* ----- for declaring memory handlers ----- */
//TODO #define DECLARE_MEM_HANDLERS_8BIT(abits) \
//TODO DECLARE_HANDLERS_8BIT(mem, abits) \
//TODO void     cpu_setopbase##abits              (offs_t pc);
//TODO 
//TODO #define DECLARE_MEM_HANDLERS_16BIT_BE(abits) \
//TODO DECLARE_HANDLERS_16BIT_BE(mem, abits) \
//TODO void     cpu_setopbase##abits##bew         (offs_t pc);
//TODO 
//TODO #define DECLARE_MEM_HANDLERS_16BIT_LE(abits) \
//TODO DECLARE_HANDLERS_16BIT_LE(mem, abits) \
//TODO void     cpu_setopbase##abits##lew         (offs_t pc);
//TODO 
//TODO #define DECLARE_MEM_HANDLERS_32BIT_BE(abits) \
//TODO DECLARE_HANDLERS_32BIT_BE(mem, abits) \
//TODO void     cpu_setopbase##abits##bedw        (offs_t pc);
//TODO 
//TODO #define DECLARE_MEM_HANDLERS_32BIT_LE(abits) \
//TODO DECLARE_HANDLERS_32BIT_LE(mem, abits) \
//TODO void     cpu_setopbase##abits##ledw        (offs_t pc);
//TODO 
//TODO /* ----- for declaring port handlers ----- */
//TODO #define DECLARE_PORT_HANDLERS_8BIT(abits) \
//TODO DECLARE_HANDLERS_8BIT(port, abits)
//TODO 
//TODO #define DECLARE_PORT_HANDLERS_16BIT_BE(abits) \
//TODO DECLARE_HANDLERS_16BIT_BE(port, abits)
//TODO 
//TODO #define DECLARE_PORT_HANDLERS_16BIT_LE(abits) \
//TODO DECLARE_HANDLERS_16BIT_LE(port, abits)
//TODO 
//TODO #define DECLARE_PORT_HANDLERS_32BIT_BE(abits) \
//TODO DECLARE_HANDLERS_32BIT_BE(port, abits)
//TODO 
//TODO #define DECLARE_PORT_HANDLERS_32BIT_LE(abits) \
//TODO DECLARE_HANDLERS_32BIT_LE(port, abits)
//TODO 
//TODO 
//TODO 
//TODO /***************************************************************************
//TODO 
//TODO 	Function prototypes for core readmem/writemem routines
//TODO 
//TODO ***************************************************************************/
//TODO 
//TODO /* ----- declare 8-bit handlers ----- */
//TODO DECLARE_MEM_HANDLERS_8BIT(16)
//TODO DECLARE_MEM_HANDLERS_8BIT(17)
//TODO DECLARE_MEM_HANDLERS_8BIT(20)
//TODO DECLARE_MEM_HANDLERS_8BIT(21)
//TODO DECLARE_MEM_HANDLERS_8BIT(24)
//TODO #define change_pc16(pc)			change_pc_generic(pc, 16, 0, cpu_setopbase16)
//TODO #define change_pc17(pc) 		change_pc_generic(pc, 17, 0, cpu_setopbase17)
//TODO #define change_pc20(pc)			change_pc_generic(pc, 20, 0, cpu_setopbase20)
//TODO #define change_pc21(pc)			change_pc_generic(pc, 21, 0, cpu_setopbase21)
//TODO #define change_pc24(pc)			change_pc_generic(pc, 24, 0, cpu_setopbase24)
//TODO 
//TODO /* ----- declare 16-bit bigendian handlers ----- */
//TODO DECLARE_MEM_HANDLERS_16BIT_BE(16)
//TODO DECLARE_MEM_HANDLERS_16BIT_BE(18)
//TODO DECLARE_MEM_HANDLERS_16BIT_BE(24)
//TODO DECLARE_MEM_HANDLERS_16BIT_BE(32)
//TODO #define change_pc16bew(pc)		change_pc_generic(pc, 16, 1, cpu_setopbase16bew)
//TODO #define change_pc18bew(pc)      change_pc_generic(pc, 18, 1, cpu_setopbase18bew)
//TODO #define change_pc24bew(pc)		change_pc_generic(pc, 24, 1, cpu_setopbase24bew)
//TODO #define change_pc32bew(pc)		change_pc_generic(pc, 32, 1, cpu_setopbase32bew)
//TODO 
//TODO /* ----- declare 16-bit littleendian handlers ----- */
//TODO DECLARE_MEM_HANDLERS_16BIT_LE(16)
//TODO DECLARE_MEM_HANDLERS_16BIT_LE(17)
//TODO DECLARE_MEM_HANDLERS_16BIT_LE(24)
//TODO DECLARE_MEM_HANDLERS_16BIT_LE(29)
//TODO DECLARE_MEM_HANDLERS_16BIT_LE(32)
//TODO #define change_pc16lew(pc)		change_pc_generic(pc, 16, 1, cpu_setopbase16lew)
//TODO #define change_pc17lew(pc)		change_pc_generic(pc, 17, 1, cpu_setopbase17lew)
//TODO #define change_pc24lew(pc)		change_pc_generic(pc, 24, 1, cpu_setopbase24lew)
//TODO #define change_pc29lew(pc)		change_pc_generic(pc, 29, 1, cpu_setopbase29lew)
//TODO #define change_pc32lew(pc)		change_pc_generic(pc, 32, 1, cpu_setopbase32lew)
//TODO 
//TODO /* ----- declare 32-bit bigendian handlers ----- */
//TODO DECLARE_MEM_HANDLERS_32BIT_BE(24)
//TODO DECLARE_MEM_HANDLERS_32BIT_BE(29)
//TODO DECLARE_MEM_HANDLERS_32BIT_BE(32)
//TODO #define change_pc24bedw(pc)		change_pc_generic(pc, 24, 2, cpu_setopbase24bedw)
//TODO #define change_pc29bedw(pc)		change_pc_generic(pc, 29, 2, cpu_setopbase29bedw)
//TODO #define change_pc32bedw(pc)		change_pc_generic(pc, 32, 2, cpu_setopbase32bedw)
//TODO 
//TODO /* ----- declare 32-bit littleendian handlers ----- */
//TODO DECLARE_MEM_HANDLERS_32BIT_LE(24)
//TODO DECLARE_MEM_HANDLERS_32BIT_LE(26)
//TODO DECLARE_MEM_HANDLERS_32BIT_LE(29)
//TODO DECLARE_MEM_HANDLERS_32BIT_LE(32)
//TODO #define change_pc24ledw(pc)		change_pc_generic(pc, 24, 2, cpu_setopbase24ledw)
//TODO #define change_pc26ledw(pc)		change_pc_generic(pc, 26, 2, cpu_setopbase26ledw)
//TODO #define change_pc29ledw(pc)		change_pc_generic(pc, 29, 2, cpu_setopbase29ledw)
//TODO #define change_pc32ledw(pc)		change_pc_generic(pc, 32, 2, cpu_setopbase32ledw)
//TODO 
//TODO /* ----- declare pdp1 handler ----- */
//TODO DECLARE_MEM_HANDLERS_32BIT_BE(18)
//TODO #define change_pc28bedw(pc)		change_pc_generic(pc, 18, 2, cpu_setopbase18bedw)
//TODO 
//TODO 
//TODO /***************************************************************************
//TODO 
//TODO 	Function prototypes for core readport/writeport routines
//TODO 
//TODO ***************************************************************************/
//TODO 
//TODO /* ----- declare 8-bit handlers ----- */
//TODO DECLARE_PORT_HANDLERS_8BIT(16)
//TODO 
//TODO /* ----- declare 16-bit bigendian handlers ----- */
//TODO DECLARE_PORT_HANDLERS_16BIT_BE(16)
//TODO 
//TODO /* ----- declare 16-bit littleendian handlers ----- */
//TODO DECLARE_PORT_HANDLERS_16BIT_LE(16)
//TODO DECLARE_PORT_HANDLERS_16BIT_LE(24)
//TODO 
//TODO /* ----- declare 32-bit bigendian handlers ----- */
//TODO DECLARE_PORT_HANDLERS_32BIT_BE(16)
//TODO 
//TODO /* ----- declare 32-bit littleendian handlers ----- */
//TODO DECLARE_PORT_HANDLERS_32BIT_LE(16)
//TODO DECLARE_PORT_HANDLERS_32BIT_LE(24)
//TODO DECLARE_PORT_HANDLERS_32BIT_LE(32)
//TODO 
//TODO 
//TODO /***************************************************************************
//TODO 
//TODO 	Function prototypes for core memory functions
//TODO 
//TODO ***************************************************************************/
//TODO 
//TODO /* ----- memory setup function ----- */
//TODO int			memory_init(void);
//TODO void		memory_shutdown(void);
//TODO void		memory_set_context(int activecpu);
//TODO void		memory_set_unmap_value(data32_t value);
//TODO 
//TODO /* ----- dynamic bank handlers ----- */
//TODO void		memory_set_bankhandler_r(int bank, offs_t offset, mem_read_handler handler);
//TODO void		memory_set_bankhandler_w(int bank, offs_t offset, mem_write_handler handler);
//TODO 
//TODO /* ----- opcode base control ---- */
//TODO opbase_handler memory_set_opbase_handler(int cpunum, opbase_handler function);
//TODO 
//TODO /* ----- separate opcode/data encryption helpers ---- */
//TODO void		memory_set_opcode_base(int cpunum, void *base);
//TODO void		memory_set_encrypted_opcode_range(int cpunum, offs_t min_address,offs_t max_address);
//TODO extern offs_t encrypted_opcode_start[],encrypted_opcode_end[];
//TODO 
//TODO /* ----- return a base pointer to memory ---- */
//TODO void *		memory_find_base(int cpunum, offs_t offset);
//TODO void *		memory_get_read_ptr(int cpunum, offs_t offset);
//TODO void *		memory_get_write_ptr(int cpunum, offs_t offset);
//TODO 
//TODO /* ----- dynamic memory mapping ----- */
//TODO data8_t *	install_mem_read_handler    (int cpunum, offs_t start, offs_t end, mem_read_handler handler);
//TODO data16_t *	install_mem_read16_handler  (int cpunum, offs_t start, offs_t end, mem_read16_handler handler);
//TODO data32_t *	install_mem_read32_handler  (int cpunum, offs_t start, offs_t end, mem_read32_handler handler);
//TODO data8_t *	install_mem_write_handler   (int cpunum, offs_t start, offs_t end, mem_write_handler handler);
//TODO data16_t *	install_mem_write16_handler (int cpunum, offs_t start, offs_t end, mem_write16_handler handler);
//TODO data32_t *	install_mem_write32_handler (int cpunum, offs_t start, offs_t end, mem_write32_handler handler);
//TODO 
//TODO /* ----- dynamic port mapping ----- */
//TODO void		install_port_read_handler   (int cpunum, offs_t start, offs_t end, port_read_handler handler);
//TODO void		install_port_read16_handler (int cpunum, offs_t start, offs_t end, port_read16_handler handler);
//TODO void		install_port_read32_handler (int cpunum, offs_t start, offs_t end, port_read32_handler handler);
//TODO void		install_port_write_handler  (int cpunum, offs_t start, offs_t end, port_write_handler handler);
//TODO void		install_port_write16_handler(int cpunum, offs_t start, offs_t end, port_write16_handler handler);
//TODO void		install_port_write32_handler(int cpunum, offs_t start, offs_t end, port_write32_handler handler);
//TODO 
//TODO 
//TODO 
//TODO /***************************************************************************
//TODO 
//TODO 	Global variables
//TODO 
//TODO ***************************************************************************/
//TODO 
//TODO extern UINT8 			opcode_entry;		/* current entry for opcode fetching */
//TODO extern UINT8 *			OP_ROM;				/* opcode ROM base */
//TODO extern UINT8 *			OP_RAM;				/* opcode RAM base */
//TODO extern UINT8 *			cpu_bankbase[];		/* array of bank bases */
//TODO extern UINT8 *			readmem_lookup;		/* pointer to the readmem lookup table */
//TODO extern offs_t			mem_amask;			/* memory address mask */
//TODO extern struct ExtMemory	ext_memory[];		/* externally-allocated memory */
//TODO 
//TODO 
//TODO 
//TODO /***************************************************************************
//TODO 
//TODO 	Helper macros
//TODO 
//TODO ***************************************************************************/
//TODO 
//TODO /* ----- 16/32-bit memory accessing ----- */
//TODO #define COMBINE_DATA(varptr)		(*(varptr) = (*(varptr) & mem_mask) | (data & ~mem_mask))
//TODO 
//TODO /* ----- 16-bit memory accessing ----- */
//TODO #define ACCESSING_LSB16				((mem_mask & 0x00ff) == 0)
//TODO #define ACCESSING_MSB16				((mem_mask & 0xff00) == 0)
//TODO #define ACCESSING_LSB				ACCESSING_LSB16
//TODO #define ACCESSING_MSB				ACCESSING_MSB16
//TODO 
//TODO /* ----- 32-bit memory accessing ----- */
//TODO #define ACCESSING_LSW32				((mem_mask & 0x0000ffff) == 0)
//TODO #define ACCESSING_MSW32				((mem_mask & 0xffff0000) == 0)
//TODO #define ACCESSING_LSB32				((mem_mask & 0x000000ff) == 0)
//TODO #define ACCESSING_MSB32				((mem_mask & 0xff000000) == 0)
//TODO 
//TODO /* ----- opcode reading ----- */
//TODO #define cpu_readop(A)				(OP_ROM[(A) & mem_amask])
//TODO #define cpu_readop16(A)				(*(data16_t *)&OP_ROM[(A) & mem_amask])
//TODO #define cpu_readop32(A)				(*(data32_t *)&OP_ROM[(A) & mem_amask])
//TODO 
//TODO /* ----- opcode argument reading ----- */
//TODO #define cpu_readop_arg(A)			(OP_RAM[(A) & mem_amask])
//TODO #define cpu_readop_arg16(A)			(*(data16_t *)&OP_RAM[(A) & mem_amask])
//TODO #define cpu_readop_arg32(A)			(*(data32_t *)&OP_RAM[(A) & mem_amask])
//TODO 
//TODO /* ----- bank switching for CPU cores ----- */
//TODO #define change_pc_generic(pc,abits,minbits,setop)										\
//TODO do {																					\
//TODO 	if (readmem_lookup[LEVEL1_INDEX((pc) & mem_amask,abits,minbits)] != opcode_entry)	\
//TODO 		setop(pc);																		\
//TODO } while (0)																				\
//TODO 
//TODO 
//TODO /* ----- forces the next branch to generate a call to the opbase handler ----- */
//TODO #define catch_nextBranch()			(opcode_entry = 0xff)
//TODO 
//TODO /* ----- bank switching macro ----- */
//TODO #define cpu_setbank(bank, base) 														\
//TODO do {																					\
//TODO 	if (bank >= STATIC_BANK1 && bank <= STATIC_BANKMAX)									\
//TODO 	{																					\
//TODO 		cpu_bankbase[bank] = (UINT8 *)(base);											\
//TODO 		if (opcode_entry == bank && cpu_getactivecpu() >= 0)							\
//TODO 		{																				\
//TODO 			opcode_entry = 0xff;														\
//TODO 			activecpu_set_op_base(activecpu_get_pc_byte());											\
//TODO 		}																				\
//TODO 	}																					\
//TODO } while (0)
//TODO 
//TODO 
//TODO 
//TODO #ifdef __cplusplus
//TODO }
//TODO #endif
//TODO 
//TODO #endif	/* !_MEMORY_H */
//TODO 
//TODO     
}
