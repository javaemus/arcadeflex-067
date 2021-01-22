/*
 * ported to v0.67
 * 
 */
package gr.codebb.arcadeflex.v067.mame;

import static gr.codebb.arcadeflex.v067.mame.driverH.*;

import static gr.codebb.arcadeflex.v067.drivers.minivadr.*;

public class driver {
//TODO /******************************************************************************
//TODO 
//TODO   driver.c
//TODO 
//TODO   The list of all available drivers. Drivers have to be included here to be
//TODO   recognized by the executable.
//TODO 
//TODO   To save some typing, we use a hack here. This file is recursively #included
//TODO   twice, with different definitions of the DRIVER() macro. The first one
//TODO   declares external references to the drivers; the second one builds an array
//TODO   storing all the drivers.
//TODO 
//TODO ******************************************************************************/
//TODO 
//TODO #include "driver.h"
//TODO 
//TODO 
//TODO #ifndef DRIVER_RECURSIVE
//TODO 
//TODO /* The "root" driver, defined so we can have &driver_##NAME in macros. */
//TODO struct GameDriver driver_0 =
//TODO {
//TODO 	__FILE__,
//TODO 	0,
//TODO 	"",
//TODO 	0,
//TODO 	0,
//TODO 	0,
//TODO 	0,
//TODO 	0,
//TODO 	0,
//TODO 	0,
//TODO 	NOT_A_DRIVER
//TODO };
//TODO 
//TODO #endif
//TODO 
//TODO #ifdef TINY_COMPILE
//TODO 
//TODO extern struct GameDriver TINY_NAME;
//TODO 
//TODO const struct GameDriver *drivers[] =
//TODO {
//TODO 	TINY_POINTER,
//TODO 	0	/* end of array */
//TODO };
//TODO 
//TODO const struct GameDriver *test_drivers[] =
//TODO {
//TODO 	0	/* end of array */
//TODO };
//TODO 
//TODO #else
//TODO 
//TODO #ifndef DRIVER_RECURSIVE
//TODO 
//TODO #define DRIVER_RECURSIVE
//TODO 
//TODO /* step 1: declare all external references */
//TODO #define DRIVER(NAME) extern struct GameDriver driver_##NAME;
//TODO #define TESTDRIVER(NAME) extern struct GameDriver driver_##NAME;
//TODO #include "driver.c"
//TODO 
//TODO /* step 2: define the drivers[] array */
//TODO #undef DRIVER
//TODO #undef TESTDRIVER
//TODO #define DRIVER(NAME) &driver_##NAME,
//TODO #define TESTDRIVER(NAME)

    public static GameDriver drivers[] = {
        /*minivadr*/driver_minivadr,
        //TODO {
        //TODO #include "driver.c"
        //TODO 	0	/* end of array */
        //TODO };
        //TODO 
        //TODO /* step 2: define the test_drivers[] array */
        //TODO #undef DRIVER
        //TODO #undef TESTDRIVER
        //TODO #define DRIVER(NAME)
        //TODO #define TESTDRIVER(NAME) &driver_##NAME,
        //TODO const struct GameDriver *test_drivers[] =
        //TODO {
        //TODO #include "driver.c"
        //TODO 	0	/* end of array */
        //TODO };
        //TODO 
        //TODO #else	/* DRIVER_RECURSIVE */
        //TODO 
        //TODO #ifndef NEOMAME
        //TODO #ifndef CPSMAME
        //TODO 
        //TODO 	/* "Pacman hardware" games */
        //TODO 	DRIVER( puckman )	/* (c) 1980 Namco */
        //TODO 	DRIVER( puckmana )	/* (c) 1980 Namco */
        //TODO 	DRIVER( pacman )	/* (c) 1980 Midway */
        //TODO 	DRIVER( puckmod )	/* (c) 1981 Namco */
        //TODO 	DRIVER( pacmod )	/* (c) 1981 Midway */
        //TODO 	DRIVER( hangly )	/* hack */
        //TODO 	DRIVER( hangly2 )	/* hack */
        //TODO 	DRIVER( newpuckx )	/* hack */
        //TODO 	DRIVER( pacheart )	/* hack */
        //TODO 	DRIVER( joyman )	/* hack */
        //TODO 	DRIVER( piranha )	/* hack */
        //TODO 	DRIVER( pacplus )
        //TODO 	DRIVER( mspacman )	/* (c) 1981 Midway */	/* made by Gencomp */
        //TODO 	DRIVER( mspacmab )	/* bootleg */
        //TODO 	DRIVER( mspacmat )	/* hack */
        //TODO 	DRIVER( mspacpls )	/* hack */
        //TODO 	DRIVER( pacgal )	/* hack */
        //TODO 	DRIVER( mschamp )	/* hack */
        //TODO 	DRIVER( maketrax )	/* (c) 1981 Williams, high score table says KRL (fur Kural) */
        //TODO 	DRIVER( crush )		/* (c) 1981 Kural Samno Electric Ltd */
        //TODO 	DRIVER( crush2 )	/* (c) 1981 Kural Esco Electric Ltd - bootleg? */
        //TODO 	DRIVER( crush3 )	/* Kural Electric Ltd - bootleg? */
        //TODO 	DRIVER( mbrush )	/* 1981 bootleg */
        //TODO 	DRIVER( paintrlr )	/* 1981 bootleg */
        //TODO 	DRIVER( eyes )		/* (c) 1982 Digitrex Techstar + "Rockola presents" */
        //TODO 	DRIVER( eyes2 )		/* (c) 1982 Techstar + "Rockola presents" */
        //TODO 	DRIVER( mrtnt )		/* (c) 1983 Telko */
        //TODO 	DRIVER( ponpoko )	/* (c) 1982 Sigma Ent. Inc. */
        //TODO 	DRIVER( ponpokov )	/* (c) 1982 Sigma Ent. Inc. + Venture Line license */
        //TODO 	DRIVER( lizwiz )	/* (c) 1985 Techstar + "Sunn presents" */
        //TODO 	DRIVER( theglobp )	/* (c) 1983 Epos Corporation */
        //TODO 	DRIVER( beastf )	/* (c) 1984 Epos Corporation */
        //TODO 	DRIVER( dremshpr )	/* (c) 1982 Sanritsu */
        //TODO 	DRIVER( vanvan )	/* (c) 1983 Sanritsu */
        //TODO 	DRIVER( vanvank )	/* (c) 1983 Karateco (bootleg?) */
        //TODO 	DRIVER( alibaba )	/* (c) 1982 Sega */
        //TODO 	DRIVER( pengo )		/* 834-0386 (c) 1982 Sega */
        //TODO 	DRIVER( pengo2 )	/* 834-0386 (c) 1982 Sega */
        //TODO 	DRIVER( pengo2u )	/* 834-0386 (c) 1982 Sega */
        //TODO 	DRIVER( pengob )	/* bootleg */
        //TODO 	DRIVER( penta )		/* bootleg */
        //TODO 	DRIVER( jrpacman )	/* (c) 1983 Midway */
        //TODO 	DRIVER( jumpshot )	/* (c) 1985 Bally Midway */
        //TODO 	DRIVER( shootbul )	/* (c) 1985 Bally Midway */
        //TODO 
        //TODO 	/* Epos games */
        //TODO 	DRIVER( megadon )	/* (c) 1982 */
        //TODO 	DRIVER( catapult )	/* (c) 1982 */
        //TODO 	DRIVER( suprglob )	/* (c) 1983 */
        //TODO 	DRIVER( theglob )	/* (c) 1983 */
        //TODO 	DRIVER( theglob2 )	/* (c) 1983 */
        //TODO 	DRIVER( theglob3 )	/* (c) 1983 */
        //TODO 	DRIVER( igmo )		/* (c) 1984 */
        //TODO 
        //TODO 	/* "Galaxian hardware" games */
        //TODO 	DRIVER( galaxian )	/* (c) Namco */
        //TODO 	DRIVER( galaxiaj )	/* (c) Namco */
        //TODO 	DRIVER( galmidw )	/* (c) Midway */
        //TODO 	DRIVER( galmidwo )	/* (c) Midway */
        //TODO 	DRIVER( superg )	/* hack */
        //TODO 	DRIVER( galapx )	/* hack */
        //TODO 	DRIVER( galap1 )	/* hack */
        //TODO 	DRIVER( galap4 )	/* hack */
        //TODO 	DRIVER( galturbo )	/* hack */
        //TODO 	DRIVER( swarm )		/* hack */
        //TODO 	DRIVER( zerotime )	/* hack */
        //TODO 	DRIVER( gmgalax )	/* bootleg */
        //TODO 	DRIVER( pisces )	/* Subelectro */
        //TODO 	DRIVER( piscesb )	/* bootleg */
        //TODO 	DRIVER( uniwars )	/* (c) Irem */
        //TODO 	DRIVER( gteikoku )	/* (c) Irem */
        //TODO 	DRIVER( gteikokb )	/* bootleg */
        //TODO 	DRIVER( gteikob2 )	/* bootleg */
        //TODO 	DRIVER( spacbatt )	/* bootleg */
        //TODO 	DRIVER( batman2 )	/* bootleg */
        //TODO 	DRIVER( warofbug )	/* (c) 1981 Armenia */
        //TODO 	DRIVER( redufo )	/* bootleg - original should be (c) Artic */
        //TODO 	DRIVER( exodus )	/* Subelectro - bootleg? */
        //TODO 	DRIVER( streakng )	/* [1980] Shoei */
        //TODO 	DRIVER( pacmanbl )	/* bootleg */
        //TODO 	DRIVER( devilfsg )	/* (c) 1984 Vision / Artic (bootleg?) */
        //TODO 	DRIVER( zigzag )	/* (c) 1982 LAX */
        //TODO 	DRIVER( zigzag2 )	/* (c) 1982 LAX */
        //TODO 	DRIVER( jumpbug )	/* (c) 1981 Rock-ola */
        //TODO 	DRIVER( jumpbugb )	/* (c) 1981 Sega */
        //TODO 	DRIVER( levers )	/* (c) 1983 Rock-ola */
        //TODO 	DRIVER( azurian )	/* (c) 1982 Rait Electronics Ltd */
        //TODO 	DRIVER( orbitron )	/* Signatron USA */
        //TODO 	DRIVER( mooncrgx )	/* bootleg */
        //TODO 	DRIVER( mooncrst )	/* (c) 1980 Nichibutsu */
        //TODO 	DRIVER( mooncrsu )	/* (c) 1980 Nichibutsu USA */
        //TODO 	DRIVER( mooncrsa )	/* (c) 1980 Nichibutsu */
        //TODO 	DRIVER( mooncrsg )	/* (c) 1980 Gremlin */
        //TODO 	DRIVER( smooncrs )	/* Gremlin */
        //TODO 	DRIVER( mooncrsb )	/* bootleg */
        //TODO 	DRIVER( mooncrs2 )	/* bootleg */
        //TODO 	DRIVER( fantazia )	/* bootleg */
        //TODO 	DRIVER( moonqsr )	/* (c) 1980 Nichibutsu */
        //TODO 	DRIVER( mshuttle )	/* (c) 1980 Nichibutsu */
        //TODO 	DRIVER( mshuttlj )	/* (c) 1980 Nichibutsu */
        //TODO 	DRIVER( moonal2 )	/* Nichibutsu */
        //TODO 	DRIVER( moonal2b )	/* Nichibutsu */
        //TODO 	DRIVER( eagle )		/* (c) Centuri */
        //TODO 	DRIVER( eagle2 )	/* (c) Centuri */
        //TODO 	DRIVER( skybase )	/* (c) 1982 Omori Electric Co., Ltd. */
        //TODO 	DRIVER( checkman )	/* (c) 1982 Zilec-Zenitone */
        //TODO 	DRIVER( checkmaj )	/* (c) 1982 Jaleco (Zenitone/Zilec in ROM CM4, and the programmer names) */
        //TODO 	DRIVER( dingo )		/* (c) 1983 Ashby Computers and Graphics LTD. + Jaleco license */
        //TODO 	DRIVER( blkhole )	/* TDS (Tokyo Denshi Sekkei) */
        //TODO 	DRIVER( kingball )	/* (c) 1980 Namco */
        //TODO 	DRIVER( kingbalj )	/* (c) 1980 Namco */
        //TODO 	DRIVER( scorpnmc )	/* bootleg */
        //TODO 	DRIVER( frogg )		/* bootleg */
        //TODO 	DRIVER( 4in1 )		/* (c) 1981 Armenia / Food and Fun */
        //TODO 	DRIVER( bagmanmc )	/* bootleg */
        //TODO 	DRIVER( dkongjrm )	/* bootleg */
        //TODO 
        //TODO 	/* "Scramble hardware" (and variations) games */
        //TODO 	DRIVER( scramble )	/* GX387 (c) 1981 Konami */
        //TODO 	DRIVER( scrambls )	/* GX387 (c) 1981 Stern */
        //TODO 	DRIVER( scramblb )	/* bootleg */
        //TODO 	DRIVER( atlantis )	/* (c) 1981 Comsoft */
        //TODO 	DRIVER( atlants2 )	/* (c) 1981 Comsoft */
        //TODO 	DRIVER( theend )	/* (c) 1980 Konami */
        //TODO 	DRIVER( theends )	/* (c) 1980 Stern */
        //TODO 	DRIVER( omega )		/* bootleg */
        //TODO 	DRIVER( ckongs )	/* bootleg */
        //TODO 	DRIVER( froggers )	/* bootleg */
        //TODO 	DRIVER( amidars )	/* GX337 (c) 1982 Konami */
        //TODO 	DRIVER( triplep )	/* (c) 1982 KKI */	/* made by Sanritsu? */
        //TODO 	DRIVER( knockout )	/* (c) 1982 KKK */
        //TODO 	DRIVER( mariner )	/* (c) 1981 Amenip */
        //TODO 	DRIVER( 800fath )	/* (c) 1981 Amenip + U.S. Billiards license */
        //TODO 	DRIVER( mars )		/* (c) 1981 Artic */
        //TODO 	DRIVER( devilfsh )	/* (c) 1982 Artic */
        //TODO 	DRIVER( newsin7 )	/* (c) 1983 ATW USA, Inc. */
        //TODO 	DRIVER( mrkougar )	/* (c) 1984 ATW */
        //TODO 	DRIVER( mrkougr2 )
        //TODO 	DRIVER( mrkougb )	/* bootleg */
        //TODO 	DRIVER( hotshock )	/* (c) 1982 E.G. Felaco */
        //TODO 	DRIVER( hunchbks )	/* (c) 1983 Century */
        //TODO 	DRIVER( cavelon )	/* (c) 1983 Jetsoft */
        //TODO 	DRIVER( sfx )
        //TODO 	DRIVER( mimonkey )
        //TODO 	DRIVER( mimonksc )
        //TODO 	DRIVER( scobra )	/* GX316 (c) 1981 Konami */
        //TODO 	DRIVER( scobras )	/* GX316 (c) 1981 Stern */
        //TODO 	DRIVER( scobrase )	/* GX316 (c) 1981 Stern */
        //TODO 	DRIVER( scobrab )	/* GX316 (c) 1981 Karateco (bootleg?) */
        //TODO 	DRIVER( stratgyx )	/* GX306 (c) 1981 Konami */
        //TODO 	DRIVER( stratgys )	/* GX306 (c) 1981 Stern */
        //TODO 	DRIVER( armorcar )	/* (c) 1981 Stern */
        //TODO 	DRIVER( armorca2 )	/* (c) 1981 Stern */
        //TODO 	DRIVER( moonwar )	/* (c) 1981 Stern */
        //TODO 	DRIVER( moonwara )	/* (c) 1981 Stern */
        //TODO 	DRIVER( spdcoin )	/* (c) 1984 Stern */
        //TODO 	DRIVER( darkplnt )	/* (c) 1982 Stern */
        //TODO 	DRIVER( tazmania )	/* (c) 1982 Stern */
        //TODO 	DRIVER( tazmani2 )	/* (c) 1982 Stern */
        //TODO 	DRIVER( calipso )	/* (c) 1982 Tago */
        //TODO 	DRIVER( anteater )	/* (c) 1982 Tago */
        //TODO 	DRIVER( rescue )	/* (c) 1982 Stern */
        //TODO 	DRIVER( minefld )	/* (c) 1983 Stern */
        //TODO 	DRIVER( losttomb )	/* (c) 1982 Stern */
        //TODO 	DRIVER( losttmbh )	/* (c) 1982 Stern */
        //TODO 	DRIVER( superbon )	/* bootleg */
        //TODO 	DRIVER( hustler )	/* GX343 (c) 1981 Konami */
        //TODO 	DRIVER( billiard )	/* bootleg */
        //TODO 	DRIVER( hustlerb )	/* bootleg */
        //TODO 	DRIVER( frogger )	/* GX392 (c) 1981 Konami */
        //TODO 	DRIVER( frogseg1 )	/* (c) 1981 Sega */
        //TODO 	DRIVER( frogseg2 )	/* 834-0068 (c) 1981 Sega */
        //TODO 	DRIVER( froggrmc )	/* 800-3110 (c) 1981 Sega */
        //TODO 	DRIVER( amidar )	/* GX337 (c) 1981 Konami */
        //TODO 	DRIVER( amidaru )	/* GX337 (c) 1982 Konami + Stern license */
        //TODO 	DRIVER( amidaro )	/* GX337 (c) 1982 Konami + Olympia license */
        //TODO 	DRIVER( amigo )		/* bootleg */
        //TODO 	DRIVER( turtles )	/* (c) 1981 Stern */
        //TODO 	DRIVER( turpin )	/* (c) 1981 Sega */
        //TODO 	DRIVER( 600 )		/* GX353 (c) 1981 Konami */
        //TODO 	DRIVER( flyboy )	/* (c) 1982 Kaneko */
        //TODO 	DRIVER( flyboyb )	/* bootleg */
        //TODO 	DRIVER( fastfred )	/* (c) 1982 Atari */
        //TODO 	DRIVER( jumpcoas )	/* (c) 1983 Kaneko */
        //TODO 	DRIVER( boggy84 )	/* bootleg, original is (c)1983 Taito/Kaneko */
        //TODO 	DRIVER( redrobin )	/* (c) 1986 Elettronolo */
        //TODO 
        //TODO 	/* "Crazy Climber hardware" games */
        //TODO 	DRIVER( cclimber )	/* (c) 1980 Nichibutsu */
        //TODO 	DRIVER( cclimbrj )	/* (c) 1980 Nichibutsu */
        //TODO 	DRIVER( ccboot )	/* bootleg */
        //TODO 	DRIVER( ccboot2 )	/* bootleg */
        //TODO 	DRIVER( ckong )		/* (c) 1981 Falcon */
        //TODO 	DRIVER( ckonga )	/* (c) 1981 Falcon */
        //TODO 	DRIVER( ckongjeu )	/* bootleg */
        //TODO 	DRIVER( ckongo )	/* bootleg */
        //TODO 	DRIVER( ckongalc )	/* bootleg */
        //TODO 	DRIVER( monkeyd )	/* bootleg */
        //TODO 	DRIVER( rpatrolb )	/* bootleg */
        //TODO 	DRIVER( silvland )	/* Falcon */
        //TODO 	DRIVER( yamato )	/* (c) 1983 Sega */
        //TODO 	DRIVER( yamato2 )	/* (c) 1983 Sega */
        //TODO 	DRIVER( swimmer )	/* (c) 1982 Tehkan */
        //TODO 	DRIVER( swimmera )	/* (c) 1982 Tehkan */
        //TODO 	DRIVER( swimmerb )	/* (c) 1982 Tehkan */
        //TODO 	DRIVER( guzzler )	/* (c) 1983 Tehkan */
        //TODO 
        //TODO 	/* Nichibutsu games */
        //TODO 	DRIVER( gomoku )	/* (c) 1981 */
        //TODO 	DRIVER( wiping )	/* (c) 1982 */
        //TODO 	DRIVER( rugrats )	/* (c) 1983 */
        //TODO 	DRIVER( friskyt )	/* (c) 1981 */
        //TODO 	DRIVER( radrad )	/* (c) 1982 Nichibutsu USA */
        //TODO 	DRIVER( seicross )	/* (c) 1984 + Alice */
        //TODO 	DRIVER( sectrzon )	/* (c) 1984 + Alice */
        //TODO TESTDRIVER( firebatl )	/* (c) 1984 Taito */
        //TODO 	DRIVER( clshroad )	/* (c) 1986 Woodplace Inc. */
        //TODO 	DRIVER( tubep )		/* (c) 1984 + Fujitek */
        //TODO 	DRIVER( rjammer )	/* (c) 1984 + Alice */
        //TODO 	DRIVER( magmax )	/* (c) 1985 */
        //TODO 	DRIVER( cop01 )		/* (c) 1985 */
        //TODO 	DRIVER( cop01a )	/* (c) 1985 */
        //TODO 	DRIVER( mightguy )	/* (c) 1986 */
        //TODO 	DRIVER( terracre )	/* (c) 1985 */
        //TODO 	DRIVER( terracrb )	/* (c) 1985 */
        //TODO 	DRIVER( terracra )	/* (c) 1985 */
        //TODO 	DRIVER( amazon )	/* (c) 1986 */
        //TODO 	DRIVER( amatelas )	/* (c) 1986 */
        //TODO 	DRIVER( horekid )	/* (c) 1987 */
        //TODO 	DRIVER( horekidb )	/* bootleg */
        //TODO 	DRIVER( galivan )	/* (c) 1985 */
        //TODO 	DRIVER( galivan2 )	/* (c) 1985 */
        //TODO 	DRIVER( dangar )	/* (c) 1986 */
        //TODO 	DRIVER( dangar2 )	/* (c) 1986 */
        //TODO 	DRIVER( dangarb )	/* bootleg */
        //TODO 	DRIVER( ninjemak )	/* (c) 1986 (US?) */
        //TODO 	DRIVER( youma )		/* (c) 1986 (Japan) */
        //TODO 	DRIVER( legion )	/* (c) 1986 */
        //TODO 	DRIVER( legiono )	/* (c) 1986 */
        //TODO 	DRIVER( terraf )	/* (c) 1987 */
        //TODO 	DRIVER( terrafu )	/* (c) 1987 Nichibutsu USA */
        //TODO 	DRIVER( kodure )	/* (c) 1987 (Japan) */
        //TODO 	DRIVER( armedf )	/* (c) 1988 */
        //TODO 	DRIVER( cclimbr2 )	/* (c) 1988 (Japan) */
        //TODO 
        //TODO 	/* Nichibutsu Mahjong games */
        //TODO 	DRIVER( hyhoo )		/* (c) 1987 */
        //TODO 	DRIVER( hyhoo2 )	/* (c) 1987 */
        //TODO 
        //TODO 	DRIVER( pastelgl )	/* (c) 1985 */
        //TODO 
        //TODO 	DRIVER( crystalg )	/* (c) 1986 */
        //TODO 	DRIVER( crystal2 )	/* (c) 1986 */
        //TODO 	DRIVER( citylove )	/* (c) 1986 */
        //TODO 	DRIVER( apparel )	/* (c) 1986 Central Denshi */
        //TODO 	DRIVER( secolove )	/* (c) 1986 */
        //TODO 	DRIVER( housemnq )	/* (c) 1987 */
        //TODO 	DRIVER( housemn2 )	/* (c) 1987 */
        //TODO 	DRIVER( seiha )		/* (c) 1987 */
        //TODO 	DRIVER( seiham )	/* (c) 1987 */
        //TODO 	DRIVER( bijokkoy )	/* (c) 1987 */
        //TODO 	DRIVER( iemoto )	/* (c) 1987 */
        //TODO 	DRIVER( ojousan )	/* (c) 1987 */
        //TODO 	DRIVER( bijokkog )	/* (c) 1988 */
        //TODO 	DRIVER( orangec )	/* (c) 1988 Daiichi Denshi */
        //TODO 	DRIVER( vipclub )	/* (c) 1988 Daiichi Denshi */
        //TODO 	DRIVER( korinai )	/* (c) 1988 */
        //TODO 	DRIVER( kaguya )	/* (c) 1988 MIKI SYOUJI */
        //TODO 	DRIVER( otonano )	/* (c) 1988 Apple */
        //TODO 	DRIVER( kanatuen )	/* (c) 1988 Panac */
        //TODO 	DRIVER( mjsikaku )	/* (c) 1988 */
        //TODO 	DRIVER( mjsikakb )	/* (c) 1988 */
        //TODO 	DRIVER( mjcamera )	/* (c) 1988 MIKI SYOUJI */
        //TODO 	DRIVER( mmcamera )	/* (c) 1988 MIKI SYOUJI */
        //TODO 	DRIVER( idhimitu )	/* (c) 1989 Digital Soft */
        //TODO 
        //TODO 	DRIVER( msjiken )	/* (c) 1988 */
        //TODO 	DRIVER( hanamomo )	/* (c) 1988 */
        //TODO 	DRIVER( telmahjn )	/* (c) 1988 */
        //TODO 	DRIVER( gionbana )	/* (c) 1989 */
        //TODO 	DRIVER( mjfocus )	/* (c) 1989 */
        //TODO 	DRIVER( mjfocusm )	/* (c) 1989 */
        //TODO 	DRIVER( peepshow )	/* (c) 1989 AC */
        //TODO 	DRIVER( scandal )	/* (c) 1989 */
        //TODO 	DRIVER( scandalm )	/* (c) 1989 */
        //TODO 	DRIVER( mgmen89 )	/* (c) 1989 */
        //TODO 	DRIVER( mjnanpas )	/* (c) 1989 BROOKS */
        //TODO 	DRIVER( mjnanpaa )	/* (c) 1989 BROOKS */
        //TODO 	DRIVER( mjnanpau )	/* (c) 1989 BROOKS */
        //TODO 	DRIVER( pairsten )	/* (c) 1989 System Ten */
        //TODO 	DRIVER( bananadr )	/* (c) 1989 DIGITAL SOFT */
        //TODO 	DRIVER( mladyhtr )	/* (c) 1990 */
        //TODO 	DRIVER( chinmoku )	/* (c) 1990 */
        //TODO 	DRIVER( maiko )		/* (c) 1990 */
        //TODO 	DRIVER( club90s )	/* (c) 1990 */
        //TODO 	DRIVER( club90sa )	/* (c) 1990 */
        //TODO 	DRIVER( hanaoji )	/* (c) 1991 */
        //TODO 
        //TODO 	DRIVER( pstadium )	/* (c) 1990 */
        //TODO 	DRIVER( triplew1 )	/* (c) 1989 */
        //TODO 	DRIVER( triplew2 )	/* (c) 1990 */
        //TODO 	DRIVER( ntopstar )	/* (c) 1990 */
        //TODO 	DRIVER( mjlstory )	/* (c) 1991 */
        //TODO 	DRIVER( vanilla )	/* (c) 1991 */
        //TODO 	DRIVER( finalbny )	/* (c) 1991 */
        //TODO 	DRIVER( qmhayaku )	/* (c) 1991 */
        //TODO 	DRIVER( galkoku )	/* (c) 1989 Nichibutsu/T.R.TEC */
        //TODO 	DRIVER( hyouban )	/* (c) 1989 Nichibutsu/T.R.TEC */
        //TODO 	DRIVER( galkaika )	/* (c) 1989 Nichibutsu/T.R.TEC */
        //TODO 	DRIVER( tokyogal )	/* (c) 1989 */
        //TODO 	DRIVER( tokimbsj )	/* (c) 1989 */
        //TODO 	DRIVER( mcontest )	/* (c) 1989 */
        //TODO 	DRIVER( uchuuai )	/* (c) 1989 */
        //TODO 	DRIVER( av2mj1bb )	/* (c) 1991 MIKI SYOUJI/AV JAPAN */
        //TODO 	DRIVER( av2mj2rg )	/* (c) 1991 MIKI SYOUJI/AV JAPAN */
        //TODO 
        //TODO 	DRIVER( mjuraden )	/* (c) 1992 Nichibutsu/Yubis */
        //TODO 	DRIVER( koinomp )	/* (c) 1992 */
        //TODO 	DRIVER( patimono )	/* (c) 1992 */
        //TODO 	DRIVER( mjanbari )	/* (c) 1992 Nichibutsu/Yubis/AV JAPAN */
        //TODO 	DRIVER( ultramhm )	/* (c) 1993 Apple */
        //TODO 	DRIVER( gal10ren )	/* (c) 1993 FUJIC */
        //TODO 	DRIVER( renaiclb )	/* (c) 1993 FUJIC */
        //TODO 	DRIVER( mjlaman )	/* (c) 1993 Nichibutsu/AV JAPAN */
        //TODO 	DRIVER( mkeibaou )	/* (c) 1993 */
        //TODO 	DRIVER( pachiten )	/* (c) 1993 Nichibutsu/MIKI SYOUJI/AV JAPAN */
        //TODO 	DRIVER( sailorws )	/* (c) 1993 */
        //TODO 	DRIVER( sailorwr )	/* (c) 1993 */
        //TODO 	DRIVER( psailor1 )	/* (c) 1994 SPHINX */
        //TODO 	DRIVER( psailor2 )	/* (c) 1994 SPHINX */
        //TODO 	DRIVER( otatidai )	/* (c) 1995 SPHINX */
        //TODO 	DRIVER( ngpgal )	/* (c) 1991 */
        //TODO 	DRIVER( mjgottsu )	/* (c) 1991 */
        //TODO 	DRIVER( bakuhatu )	/* (c) 1991 */
        //TODO 	DRIVER( cmehyou )	/* (c) 1992 Nichibutsu/Kawakusu */
        //TODO 	DRIVER( mmehyou )	/* (c) 1992 Nichibutsu/Kawakusu */
        //TODO 	DRIVER( mjkoiura )	/* (c) 1992 */
        //TODO 	DRIVER( imekura )	/* (c) 1994 SPHINX/AV JAPAN */
        //TODO 	DRIVER( mscoutm )	/* (c) 1994 SPHINX/AV JAPAN */
        //TODO 	DRIVER( mjegolf )	/* (c) 1994 FUJIC/AV JAPAN */
        //TODO 
        //TODO 	DRIVER( niyanpai )	/* (c) 1996 */
        //TODO 
        //TODO 	/* "Phoenix hardware" (and variations) games */
        //TODO 	DRIVER( safarir )	/* Shin Nihon Kikaku (SNK) */
        //TODO 	DRIVER( phoenix )	/* (c) 1980 Amstar */
        //TODO 	DRIVER( phoenixa )	/* (c) 1980 Amstar + Centuri license */
        //TODO 	DRIVER( phoenixt )	/* (c) 1980 Taito */
        //TODO 	DRIVER( phoenix3 )	/* bootleg */
        //TODO 	DRIVER( phoenixc )	/* bootleg */
        //TODO 	DRIVER( condor )	/* bootleg */
        //TODO 	DRIVER( pleiads )	/* (c) 1981 Tehkan */
        //TODO 	DRIVER( pleiadbl )	/* bootleg */
        //TODO 	DRIVER( pleiadce )	/* (c) 1981 Centuri + Tehkan */
        //TODO TESTDRIVER( survival )	/* (c) 1982 Rock-ola */
        //TODO 	DRIVER( naughtyb )	/* (c) 1982 Jaleco */
        //TODO 	DRIVER( naughtya )	/* bootleg */
        //TODO 	DRIVER( naughtyc )	/* (c) 1982 Jaleco + Cinematronics */
        //TODO 	DRIVER( popflame )	/* (c) 1982 Jaleco */
        //TODO 	DRIVER( popflama )	/* (c) 1982 Jaleco */
        //TODO 	DRIVER( popflamb )	/* (c) 1982 Jaleco */
        //TODO 
        //TODO 	/* Namco games (plus some intruders on similar hardware) */
        //TODO 	DRIVER( geebee )	/* [1978] Namco */
        //TODO 	DRIVER( geebeeg )	/* [1978] Gremlin */
        //TODO 	DRIVER( bombbee )	/* [1979] Namco */
        //TODO 	DRIVER( cutieq )	/* (c) 1979 Namco */
        //TODO 	DRIVER( navalone )	/* (c) 1980 Namco */
        //TODO 	DRIVER( kaitei )	/* [1980] K.K. Tokki */
        //TODO 	DRIVER( kaitein )	/* [1980] Namco */
        //TODO 	DRIVER( sos )		/* [1980] Namco */
        //TODO 	DRIVER( tankbatt )	/* (c) 1980 Namco */
        //TODO 	DRIVER( warpwarp )	/* (c) 1981 Namco */
        //TODO 	DRIVER( warpwarr )	/* (c) 1981 Rock-ola - the high score table says "NAMCO" */
        //TODO 	DRIVER( warpwar2 )	/* (c) 1981 Rock-ola - the high score table says "NAMCO" */
        //TODO 	DRIVER( rallyx )	/* (c) 1980 Namco */
        //TODO 	DRIVER( rallyxm )	/* (c) 1980 Midway */
        //TODO 	DRIVER( nrallyx )	/* (c) 1981 Namco */
        //TODO 	DRIVER( nrallyv )	/* hack */
        //TODO 	DRIVER( jungler )	/* GX327 (c) 1981 Konami */
        //TODO 	DRIVER( junglers )	/* GX327 (c) 1981 Stern */
        //TODO 	DRIVER( tactcian )	/* GX335 (c) 1982 Sega */
        //TODO 	DRIVER( tactcan2 )	/* GX335 (c) 1981 Sega */
        //TODO 	DRIVER( locomotn )	/* GX359 (c) 1982 Konami + Centuri license */
        //TODO 	DRIVER( gutangtn )	/* GX359 (c) 1982 Konami + Sega license */
        //TODO 	DRIVER( cottong )	/* bootleg */
        //TODO 	DRIVER( commsega )	/* (c) 1983 Sega */
        //TODO 	/* the following ones all have a custom I/O chip */
        //TODO 	DRIVER( bosco )		/* (c) 1981 */
        //TODO 	DRIVER( boscoo )	/* (c) 1981 */
        //TODO 	DRIVER( boscoo2 )	/* (c) 1981 */
        //TODO 	DRIVER( boscomd )	/* (c) 1981 Midway */
        //TODO 	DRIVER( boscomdo )	/* (c) 1981 Midway */
        //TODO 	DRIVER( galaga )	/* (c) 1981 */
        //TODO 	DRIVER( galagamw )	/* (c) 1981 Midway */
        //TODO 	DRIVER( galagads )	/* hack */
        //TODO 	DRIVER( gallag )	/* bootleg */
        //TODO 	DRIVER( galagab2 )	/* bootleg */
        //TODO 	DRIVER( galaga84 )	/* hack */
        //TODO 	DRIVER( nebulbee )	/* hack */
        //TODO 	DRIVER( digdug )	/* (c) 1982 */
        //TODO 	DRIVER( digdugb )	/* (c) 1982 */
        //TODO 	DRIVER( digdugat )	/* (c) 1982 Atari */
        //TODO 	DRIVER( dzigzag )	/* bootleg */
        //TODO 	DRIVER( xevious )	/* (c) 1982 */
        //TODO 	DRIVER( xeviousa )	/* (c) 1982 + Atari license */
        //TODO 	DRIVER( xeviousb )	/* (c) 1982 + Atari license */
        //TODO 	DRIVER( xevios )	/* bootleg */
        //TODO 	DRIVER( battles )	/* bootleg */
        //TODO 	DRIVER( sxevious )	/* (c) 1984 */
        //TODO 	DRIVER( superpac )	/* (c) 1982 */
        //TODO 	DRIVER( superpcm )	/* (c) 1982 Midway */
        //TODO 	DRIVER( pacnpal )	/* (c) 1983 */
        //TODO 	DRIVER( pacnpal2 )	/* (c) 1983 */
        //TODO 	DRIVER( pacnchmp )	/* (c) 1983 */
        //TODO 	DRIVER( phozon )	/* (c) 1983 */
        //TODO 	DRIVER( mappy )		/* (c) 1983 */
        //TODO 	DRIVER( mappyj )	/* (c) 1983 */
        //TODO 	DRIVER( digdug2 )	/* (c) 1985 */
        //TODO 	DRIVER( digdug2o )	/* (c) 1985 */
        //TODO 	DRIVER( todruaga )	/* (c) 1984 */
        //TODO 	DRIVER( todruago )	/* (c) 1984 */
        //TODO 	DRIVER( motos )		/* (c) 1985 */
        //TODO 	DRIVER( grobda )	/* (c) 1984 */
        //TODO 	DRIVER( grobda2 )	/* (c) 1984 */
        //TODO 	DRIVER( grobda3 )	/* (c) 1984 */
        //TODO 	DRIVER( gaplus )	/* (c) 1984 */
        //TODO 	DRIVER( gaplusa )	/* (c) 1984 */
        //TODO 	DRIVER( gapluso )	/* (c) 1984 */
        //TODO 	DRIVER( galaga3 )	/* (c) 1984 */
        //TODO 	DRIVER( galaga3m )	/* (c) 1984 */
        //TODO 	DRIVER( galaga3a )	/* (c) 1984 */
        //TODO 	/* Libble Rabble board (first Japanese game using a 68000) */
        //TODO 	DRIVER( liblrabl )	/* (c) 1983 */
        //TODO 	DRIVER( toypop )	/* (c) 1986 */
        //TODO 	/* Z8000 games */
        //TODO 	DRIVER( polepos )	/* (c) 1982  */
        //TODO 	DRIVER( poleposa )	/* (c) 1982 + Atari license */
        //TODO 	DRIVER( polepos1 )	/* (c) 1982 Atari */
        //TODO 	DRIVER( topracer )	/* bootleg */
        //TODO 	DRIVER( polepos2 )	/* (c) 1983 */
        //TODO 	DRIVER( poleps2a )	/* (c) 1983 + Atari license */
        //TODO 	DRIVER( poleps2b )	/* bootleg */
        //TODO 	DRIVER( poleps2c )	/* bootleg */
        //TODO 	/* no custom I/O in the following, HD63701 (or compatible) microcontroller instead */
        //TODO 	DRIVER( pacland )	/* (c) 1984 */
        //TODO 	DRIVER( pacland2 )	/* (c) 1984 */
        //TODO 	DRIVER( pacland3 )	/* (c) 1984 */
        //TODO 	DRIVER( paclandm )	/* (c) 1984 Midway */
        //TODO 	DRIVER( drgnbstr )	/* (c) 1984 */
        //TODO 	DRIVER( skykid )	/* (c) 1985 */
        //TODO 	DRIVER( skykido )	/* (c) 1985 */
        //TODO 	DRIVER( skykidd )	/* (c) 1985 */
        //TODO 	DRIVER( baraduke )	/* (c) 1985 */
        //TODO 	DRIVER( baraduka )	/* (c) 1985 */
        //TODO 	DRIVER( metrocrs )	/* (c) 1985 */
        //TODO 	DRIVER( metrocra )	/* (c) 1985 */
        //TODO 
        //TODO 	/* Namco System 86 games */
        //TODO 	DRIVER( hopmappy )	/* (c) 1986 */
        //TODO 	DRIVER( skykiddx )	/* (c) 1986 */
        //TODO 	DRIVER( skykiddo )	/* (c) 1986 */
        //TODO 	DRIVER( roishtar )	/* (c) 1986 */
        //TODO 	DRIVER( genpeitd )	/* (c) 1986 */
        //TODO 	DRIVER( rthunder )	/* (c) 1986 new version */
        //TODO 	DRIVER( rthundro )	/* (c) 1986 old version */
        //TODO 	DRIVER( wndrmomo )	/* (c) 1987 */
        //TODO 
        //TODO 	/* Namco System 1 games */
        //TODO 	DRIVER( shadowld )	/* (c) 1987 */
        //TODO 	DRIVER( youkaidk )	/* (c) 1987 (Japan new version) */
        //TODO 	DRIVER( yokaidko )	/* (c) 1987 (Japan old version) */
        //TODO 	DRIVER( dspirit )	/* (c) 1987 new version */
        //TODO 	DRIVER( dspirito )	/* (c) 1987 old version */
        //TODO 	DRIVER( blazer )	/* (c) 1987 (Japan) */
        //TODO 	DRIVER( quester )	/* (c) 1987 (Japan) */
        //TODO 	DRIVER( pacmania )	/* (c) 1987 */
        //TODO 	DRIVER( pacmanij )	/* (c) 1987 (Japan) */
        //TODO 	DRIVER( galaga88 )	/* (c) 1987 */
        //TODO 	DRIVER( galag88b )	/* (c) 1987 */
        //TODO 	DRIVER( galag88j )	/* (c) 1987 (Japan) */
        //TODO 	DRIVER( ws )		/* (c) 1988 (Japan) */
        //TODO 	DRIVER( berabohm )	/* (c) 1988 (Japan) */
        //TODO 	DRIVER( beraboho )	/* (c) 1988 (Japan) */
        //TODO 	/* 1988 Alice in Wonderland (English version of Marchen maze) */
        //TODO 	DRIVER( mmaze )		/* (c) 1988 (Japan) */
        //TODO 	DRIVER( bakutotu )	/* (c) 1988 (Japan) */
        //TODO 	DRIVER( wldcourt )	/* (c) 1988 (Japan) */
        //TODO 	DRIVER( splatter )	/* (c) 1988 (Japan) */
        //TODO 	DRIVER( faceoff )	/* (c) 1988 (Japan) */
        //TODO 	DRIVER( rompers )	/* (c) 1989 (Japan) */
        //TODO 	DRIVER( romperso )	/* (c) 1989 (Japan) */
        //TODO 	DRIVER( blastoff )	/* (c) 1989 (Japan) */
        //TODO 	DRIVER( ws89 )		/* (c) 1989 (Japan) */
        //TODO 	DRIVER( dangseed )	/* (c) 1989 (Japan) */
        //TODO 	DRIVER( ws90 )		/* (c) 1990 (Japan) */
        //TODO 	DRIVER( pistoldm )	/* (c) 1990 (Japan) */
        //TODO 	DRIVER( boxyboy )	/* (c) 1990 (US) */
        //TODO 	DRIVER( soukobdx )	/* (c) 1990 (Japan) */
        //TODO 	DRIVER( puzlclub )	/* (c) 1990 (Japan) */
        //TODO 	DRIVER( tankfrce )	/* (c) 1991 (US) */
        //TODO 	DRIVER( tankfrcj )	/* (c) 1991 (Japan) */
        //TODO 
        //TODO 	/* Namco System 2 games */
        //TODO TESTDRIVER( finallap )	/* 87.12 Final Lap */
        //TODO TESTDRIVER( finalapd )	/* 87.12 Final Lap */
        //TODO TESTDRIVER( finalapc )	/* 87.12 Final Lap */
        //TODO TESTDRIVER( finlapjc )	/* 87.12 Final Lap */
        //TODO TESTDRIVER( finlapjb )	/* 87.12 Final Lap */
        //TODO 	DRIVER( assault )	/* (c) 1988 */
        //TODO 	DRIVER( assaultj )	/* (c) 1988 (Japan) */
        //TODO 	DRIVER( assaultp )	/* (c) 1988 (Japan) */
        //TODO 	DRIVER( metlhawk )	/* (c) 1988 */
        //TODO 	DRIVER( ordyne )	/* (c) 1988 */
        //TODO 	DRIVER( mirninja )	/* (c) 1988 (Japan) */
        //TODO 	DRIVER( phelios )	/* (c) 1988 (Japan) */
        //TODO 	DRIVER( dirtfoxj )	/* (c) 1989 (Japan) */
        //TODO TESTDRIVER( fourtrax )	/* 89.11 */
        //TODO 	DRIVER( valkyrie )	/* (c) 1989 (Japan) */
        //TODO 	DRIVER( finehour )	/* (c) 1989 (Japan) */
        //TODO 	DRIVER( burnforc )	/* (c) 1989 (Japan) */
        //TODO 	DRIVER( marvland )	/* (c) 1989 (US) */
        //TODO 	DRIVER( marvlanj )	/* (c) 1989 (Japan) */
        //TODO 	DRIVER( kyukaidk )	/* (c) 1990 (Japan) */
        //TODO 	DRIVER( kyukaido )	/* (c) 1990 (Japan) */
        //TODO 	DRIVER( dsaber )	/* (c) 1990 */
        //TODO 	DRIVER( dsaberj )	/* (c) 1990 (Japan) */
        //TODO TESTDRIVER( finalap2 )	/* 90.8  Final Lap 2 */
        //TODO TESTDRIVER( finalp2j )	/* 90.8  Final Lap 2 (Japan) */
        //TODO DRIVER( gollygho )	/* 91.7  Golly Ghost */
        //TODO 	DRIVER( rthun2 )	/* (c) 1990 */
        //TODO 	DRIVER( rthun2j )	/* (c) 1990 (Japan) */
        //TODO 	/* 91.3  Steel Gunner */
        //TODO 	/* 91.9  Super World Stadium */
        //TODO 	DRIVER( sgunner2 )	/* (c) 1991 (Japan) */
        //TODO 	DRIVER( cosmogng )	/* (c) 1991 (US) */
        //TODO 	DRIVER( cosmognj )	/* (c) 1991 (Japan) */
        //TODO TESTDRIVER( finalap3 )	/* 92.9  Final Lap 3 */
        //TODO 	DRIVER( luckywld )	/* (c) 1992 */
        //TODO TESTDRIVER( suzuka8h )
        //TODO 	/* 92.8  Bubble Trouble */
        //TODO 	DRIVER( sws92 )		/* (c) 1992 (Japan) */
        //TODO 	DRIVER( sws92g )	/* (c) 1992 (Japan) */
        //TODO TESTDRIVER( suzuk8h2 )
        //TODO 	DRIVER( sws93 )		/* (c) 1993 (Japan) */
        //TODO 	/* 93.6  Super World Stadium '93 */
        //TODO 
        //TODO 	/* Namco NA-1 / NA-2 System games */
        //TODO 	DRIVER( bkrtmaq )	/* (c) 1992 (Japan) */
        //TODO 	DRIVER( cgangpzl )	/* (c) 1992 (US) */
        //TODO 	DRIVER( cgangpzj )	/* (c) 1992 (Japan) */
        //TODO 	DRIVER( exvania )	/* (c) 1992 (Japan) */
        //TODO 	DRIVER( fghtatck )	/* (c) 1992 (US) */
        //TODO 	DRIVER( fa )		/* (c) 1992 (Japan) */
        //TODO 	DRIVER( knckhead )	/* (c) 1992 (World) */
        //TODO 	DRIVER( knckhedj )	/* (c) 1992 (Japan) */
        //TODO 	DRIVER( swcourt )	/* (c) 1992 (Japan) */
        //TODO 	DRIVER( emeralda )	/* (c) 1993 (Japan) */
        //TODO 	DRIVER( emerldaa )	/* (c) 1993 (Japan) */
        //TODO 	DRIVER( numanath )	/* (c) 1993 (World) */
        //TODO 	DRIVER( numanatj )	/* (c) 1993 (Japan) */
        //TODO 	DRIVER( quiztou )	/* (c) 1993 (Japan) */
        //TODO 	DRIVER( tinklpit )	/* (c) 1993 (Japan) */
        //TODO TESTDRIVER( xday2 )		/* (c) 1995 (Japan) */
        //TODO 
        //TODO 	/* Namco NB-1 / NB-2 System games */
        //TODO 	DRIVER( nebulray )	/* (c) 1994 (World) */
        //TODO 	DRIVER( nebulryj )	/* (c) 1994 (Japan) */
        //TODO 	DRIVER( ptblank )	/* (c) 1994 */
        //TODO 	DRIVER( gunbulet )	/* (c) 1994 (Japan) */
        //TODO 	DRIVER( gslgr94u )	/* (c) 1994 */
        //TODO 	DRIVER( sws95 )		/* (c) 1995 (Japan) */
        //TODO 	DRIVER( sws96 )		/* (c) 1996 (Japan) */
        //TODO 	DRIVER( sws97 )		/* (c) 1997 (Japan) */
        //TODO TESTDRIVER( vshoot )	/* (c) 1994 */
        //TODO 	DRIVER( outfxies )	/* (c) 1994 */
        //TODO 	DRIVER( outfxesj )	/* (c) 1994 (Japan) */
        //TODO TESTDRIVER( machbrkr )	/* (c) 1995 (Japan) */
        //TODO 
        //TODO 	/* Namco ND-1 games */
        //TODO 	DRIVER( ncv1 )		/* (c) 1995 */
        //TODO 	DRIVER( ncv1j )		/* (c) 1995 (Japan) */
        //TODO 	DRIVER( ncv1j2 )	/* (c) 1995 (Japan) */
        //TODO TESTDRIVER( ncv2 )		/* (c) 1996 */
        //TODO TESTDRIVER( ncv2j )		/* (c) 1996 (Japan) */
        //TODO 
        //TODO 	/* Namco System 21 games */
        //TODO 	/* 1988, Winning Run */
        //TODO 	/* 1989, Winning Run Suzuka Grand Prix */
        //TODO TESTDRIVER( winrun91 )
        //TODO 	DRIVER( solvalou )	/* (c) 1991 (Japan) */
        //TODO 	DRIVER( starblad )	/* (c) 1991 */
        //TODO /* 199?, Driver's Eyes */
        //TODO /* 1992, ShimDrive */
        //TODO TESTDRIVER( aircombj )	/* (c) 1992 (Japan) */
        //TODO TESTDRIVER( aircombu )	/* (c) 1992 (US) */
        //TODO TESTDRIVER( cybsled )	/* (c) 1993 */
        //TODO 
        //TODO 	/* Namco System 22 games */
        //TODO TESTDRIVER( alpinerd )	/* (c) 1994 */
        //TODO TESTDRIVER( raveracw )	/* (c) 1995 */
        //TODO TESTDRIVER( rr1 )
        //TODO TESTDRIVER( rrs1 )
        //TODO TESTDRIVER( victlap )
        //TODO 	DRIVER( propcycl )	/* (c) 1996 */
        //TODO 
        //TODO 	/* Universal games */
        //TODO 	DRIVER( cosmicg )	/* 7907 (c) 1979 */
        //TODO 	DRIVER( cosmica )	/* 7910 (c) [1979] */
        //TODO 	DRIVER( cosmica2 )	/* 7910 (c) 1979 */
        //TODO 	DRIVER( panic )		/* (c) 1980 */
        //TODO 	DRIVER( panica )	/* (c) 1980 */
        //TODO 	DRIVER( panicger )	/* (c) 1980 */
        //TODO 	DRIVER( zerohour )	/* 8011 (c) Universal */
        //TODO 	DRIVER( redclash )	/* (c) 1981 Tehkan */
        //TODO 	DRIVER( redclask )	/* (c) Kaneko (bootleg?) */
        //TODO 	DRIVER( magspot2 )	/* 8013 (c) [1980] */
        //TODO 	DRIVER( devzone )	/* 8022 (c) [1980] */
        //TODO 	DRIVER( nomnlnd )	/* (c) [1980?] */
        //TODO 	DRIVER( nomnlndg )	/* (c) [1980?] + Gottlieb */
        //TODO 	DRIVER( cheekyms )	/* (c) [1980?] */
        //TODO 	DRIVER( ladybug )	/* (c) 1981 */
        //TODO 	DRIVER( ladybugb )	/* bootleg */
        //TODO 	DRIVER( snapjack )	/* (c) */
        //TODO 	DRIVER( cavenger )	/* (c) 1981 */
        //TODO 	DRIVER( dorodon )	/* Falcon */
        //TODO 	DRIVER( dorodon2 )	/* Falcon */
        //TODO 	DRIVER( mrdo )		/* (c) 1982 */
        //TODO 	DRIVER( mrdoy )		/* (c) 1982 */
        //TODO 	DRIVER( mrdot )		/* (c) 1982 + Taito license */
        //TODO 	DRIVER( mrdofix )	/* (c) 1982 + Taito license */
        //TODO 	DRIVER( mrlo )		/* bootleg */
        //TODO 	DRIVER( mrdu )		/* bootleg */
        //TODO 	DRIVER( yankeedo )	/* bootleg */
        //TODO 	DRIVER( docastle )	/* (c) 1983 */
        //TODO 	DRIVER( docastl2 )	/* (c) 1983 */
        //TODO 	DRIVER( douni )		/* (c) 1983 */
        //TODO 	DRIVER( dorunrun )	/* (c) 1984 */
        //TODO 	DRIVER( dorunru2 )	/* (c) 1984 */
        //TODO 	DRIVER( dorunruc )	/* (c) 1984 */
        //TODO 	DRIVER( spiero )	/* (c) 1987 */
        //TODO 	DRIVER( dowild )	/* (c) 1984 */
        //TODO 	DRIVER( jjack )		/* (c) 1984 */
        //TODO 	DRIVER( kickridr )	/* (c) 1984 */
        //TODO 	DRIVER( idsoccer )	/* (c) 1985 */
        //TODO 
        //TODO 	/* Nintendo games */
        //TODO 	DRIVER( radarscp )	/* (c) 1980 Nintendo */
        //TODO 	DRIVER( dkong )		/* (c) 1981 Nintendo of America */
        //TODO 	DRIVER( dkongo )	/* (c) 1981 Nintendo */
        //TODO 	DRIVER( dkongjp )	/* (c) 1981 Nintendo */
        //TODO 	DRIVER( dkongjo )	/* (c) 1981 Nintendo */
        //TODO 	DRIVER( dkongjo1 )	/* (c) 1981 Nintendo */
        //TODO 	DRIVER( dkongjr )	/* (c) 1982 Nintendo of America */
        //TODO 	DRIVER( dkongjrj )	/* (c) 1982 Nintendo */
        //TODO 	DRIVER( dkngjnrj )	/* (c) 1982 Nintendo */
        //TODO 	DRIVER( dkongjrb )	/* bootleg */
        //TODO 	DRIVER( dkngjnrb )	/* (c) 1982 Nintendo of America */
        //TODO 	DRIVER( dkong3 )	/* (c) 1983 Nintendo of America */
        //TODO 	DRIVER( dkong3j )	/* (c) 1983 Nintendo */
        //TODO 	DRIVER( mario )		/* (c) 1983 Nintendo of America */
        //TODO 	DRIVER( mariojp )	/* (c) 1983 Nintendo */
        //TODO 	DRIVER( masao )		/* bootleg */
        //TODO 	DRIVER( hunchbkd )	/* (c) 1983 Century */
        //TODO 	DRIVER( herbiedk )	/* (c) 1984 CVS */
        //TODO 	DRIVER( herodk )	/* (c) 1984 Seatongrove + Crown license */
        //TODO 	DRIVER( herodku )	/* (c) 1984 Seatongrove + Crown license */
        //TODO 	DRIVER( skyskipr )	/* (c) 1981 */
        //TODO 	DRIVER( popeye )	/* (c) 1982 */
        //TODO 	DRIVER( popeyeu )	/* (c) 1982 */
        //TODO 	DRIVER( popeyef )	/* (c) 1982 */
        //TODO 	DRIVER( popeyebl )	/* bootleg */
        //TODO 	DRIVER( punchout )	/* (c) 1984 */
        //TODO 	DRIVER( spnchout )	/* (c) 1984 */
        //TODO 	DRIVER( spnchotj )	/* (c) 1984 (Japan) */
        //TODO 	DRIVER( armwrest )	/* (c) 1985 */
        //TODO 
        //TODO 	/* Nintendo Playchoice 10 games */
        //TODO 	DRIVER( pc_tenis )	/* (c) 1983 Nintendo */
        //TODO 	DRIVER( pc_mario )	/* (c) 1983 Nintendo */
        //TODO 	DRIVER( pc_bball )	/* (c) 1984 Nintendo of America */
        //TODO 	DRIVER( pc_bfght )	/* (c) 1984 Nintendo */
        //TODO 	DRIVER( pc_ebike )	/* (c) 1984 Nintendo */
        //TODO 	DRIVER( pc_golf	)	/* (c) 1984 Nintendo */
        //TODO 	DRIVER( pc_kngfu )	/* (c) 1984 Irem (Nintendo license) */
        //TODO 	DRIVER( pc_1942 )	/* (c) 1985 Capcom */
        //TODO 	DRIVER( pc_smb )	/* (c) 1985 Nintendo */
        //TODO 	DRIVER( pc_vball )	/* (c) 1986 Nintendo */
        //TODO 	DRIVER( pc_duckh )	/* (c) 1984 Nintendo */
        //TODO 	DRIVER( pc_hgaly )	/* (c) 1984 Nintendo */
        //TODO 	DRIVER( pc_wgnmn )	/* (c) 1984 Nintendo */
        //TODO 	DRIVER( pc_grdus )	/* (c) 1986 Konami */
        //TODO 	DRIVER( pc_tkfld )	/* (c) 1987 Konami (Nintendo of America license) */
        //TODO 	DRIVER( pc_pwrst )	/* (c) 1986 Nintendo */
        //TODO 	DRIVER( pc_trjan )	/* (c) 1986 Capcom USA (Nintendo of America license) */
        //TODO 	DRIVER( pc_cvnia )	/* (c) 1987 Konami (Nintendo of America license) */
        //TODO 	DRIVER( pc_dbldr )	/* (c) 1987 Konami (Nintendo of America license) */
        //TODO 	DRIVER( pc_rnatk )	/* (c) 1987 Konami (Nintendo of America license) */
        //TODO 	DRIVER( pc_rygar )	/* (c) 1987 Tecmo (Nintendo of America license) */
        //TODO 	DRIVER( pc_cntra )	/* (c) 1988 Konami (Nintendo of America license) */
        //TODO 	DRIVER( pc_goons )	/* (c) 1986 Konami */
        //TODO 	DRIVER( pc_mtoid )	/* (c) 1986 Nintendo */
        //TODO 	DRIVER( pc_radrc )	/* (c) 1987 Square */
        //TODO 	DRIVER( pc_miket )	/* (c) 1987 Nintendo */
        //TODO 	DRIVER( pc_rcpam )	/* (c) 1987 Rare */
        //TODO 	DRIVER( pc_ngaid )	/* (c) 1989 Tecmo (Nintendo of America license) */
        //TODO 	DRIVER( pc_tmnt )	/* (c) 1989 Konami (Nintendo of America license) */
        //TODO 	DRIVER( pc_ftqst )	/* (c) 1989 Sunsoft (Nintendo of America license) */
        //TODO 	DRIVER( pc_bstar )	/* (c) 1989 SNK (Nintendo of America license) */
        //TODO 	DRIVER( pc_tbowl )	/* (c) 1989 Tecmo (Nintendo of America license) */
        //TODO 	DRIVER( pc_drmro )	/* (c) 1990 Nintendo */
        //TODO 	DRIVER( pc_ynoid )	/* (c) 1990 Capcom USA (Nintendo of America license) */
        //TODO 	DRIVER( pc_rrngr )	/* (c) Capcom USA (Nintendo of America license) */
        //TODO 	DRIVER( pc_ddrgn )
        //TODO 	DRIVER( pc_gntlt )	/* (c) 1985 Atari/Tengen (Nintendo of America license) */
        //TODO 	DRIVER( pc_smb2 )	/* (c) 1988 Nintendo */
        //TODO 	DRIVER( pc_smb3 )	/* (c) 1988 Nintendo */
        //TODO 	DRIVER( pc_mman3 )	/* (c) 1990 Capcom USA (Nintendo of America license) */
        //TODO 	DRIVER( pc_radr2 )	/* (c) 1990 Square (Nintendo of America license) */
        //TODO 	DRIVER( pc_suprc )	/* (c) 1990 Konami (Nintendo of America license) */
        //TODO 	DRIVER( pc_tmnt2 )	/* (c) 1990 Konami (Nintendo of America license) */
        //TODO 	DRIVER( pc_wcup )	/* (c) 1990 Technos (Nintendo license) */
        //TODO 	DRIVER( pc_ngai2 )	/* (c) 1990 Tecmo (Nintendo of America license) */
        //TODO 	DRIVER( pc_ngai3 )	/* (c) 1991 Tecmo (Nintendo of America license) */
        //TODO 	DRIVER( pc_pwbld )	/* (c) 1991 Taito (Nintendo of America license) */
        //TODO 	DRIVER( pc_rkats )	/* (c) 1991 Atlus (Nintendo of America license) */
        //TODO TESTDRIVER( pc_pinbt )	/* (c) 1988 Rare (Nintendo of America license) */
        //TODO 	DRIVER( pc_cshwk )	/* (c) 1989 Rare (Nintendo of America license) */
        //TODO 	DRIVER( pc_sjetm )	/* (c) 1990 Rare */
        //TODO 	DRIVER( pc_moglf )	/* (c) 1991 Nintendo */
        //TODO 
        //TODO 	/* Nintendo VS games */
        //TODO 	DRIVER( btlecity )	/* (c) 1985 Namco */
        //TODO 	DRIVER( starlstr )	/* (c) 1985 Namco */
        //TODO 	DRIVER( cstlevna )	/* (c) 1987 Konami */
        //TODO 	DRIVER( cluclu )	/* (c) 1984 Nintendo */
        //TODO 	DRIVER( drmario )	/* (c) 1990 Nintendo */
        //TODO 	DRIVER( duckhunt )	/* (c) 1985 Nintendo */
        //TODO 	DRIVER( excitebk )	/* (c) 1984 Nintendo */
        //TODO 	DRIVER( excitbkj )	/* (c) 1984 Nintendo */
        //TODO 	DRIVER( goonies )	/* (c) 1986 Konami */
        //TODO 	DRIVER( hogalley )	/* (c) 1985 Nintendo */
        //TODO 	DRIVER( iceclimb )	/* (c) 1984 Nintendo */
        //TODO 	DRIVER( iceclmbj )	/* (c) 1984 Nintendo */
        //TODO 	DRIVER( ladygolf )	/* (c) 1984 Nintendo */
        //TODO 	DRIVER( machridr )	/* (c) 1985 Nintendo */
        //TODO 	DRIVER( machridj )	/* (c) 1985 Nintendo */
        //TODO 	DRIVER( rbibb )		/* (c) 1987 Namco */
        //TODO 	DRIVER( suprmrio )	/* (c) 1986 Nintendo */
        //TODO 	DRIVER( vsskykid )	/* (c) 1986 Namco */
        //TODO 	DRIVER( tkoboxng )	/* (c) 1987 Data East */
        //TODO 	DRIVER( smgolf )	/* (c) 1984 Nintendo */
        //TODO 	DRIVER( smgolfj )	/* (c) 1984 Nintendo */
        //TODO 	DRIVER( vspinbal )	/* (c) 1984 Nintendo */
        //TODO 	DRIVER( vspinblj )	/* (c) 1984 Nintendo */
        //TODO 	DRIVER( vsslalom )	/* (c) 1986 Nintendo */
        //TODO 	DRIVER( vssoccer )	/* (c) 1985 Nintendo */
        //TODO 	DRIVER( vsgradus )	/* (c) 1986 Konami */
        //TODO 	DRIVER( platoon )	/* (c) 1987 Ocean */
        //TODO 	DRIVER( vstetris )	/* (c) 1988 Atari */
        //TODO 	DRIVER( mightybj )	/* (c) 1986 Tecmo */
        //TODO 	DRIVER( jajamaru )	/* (c) 1985 Jaleco */
        //TODO 	DRIVER( topgun )	/* (c) 1987 Konami */
        //TODO 	DRIVER( bnglngby )	/* (c) 1985 Nintendo / Broderbund Software Inc. */
        //TODO 	DRIVER( vstennis )	/* (c) 1984 Nintendo */
        //TODO 	DRIVER( wrecking )	/* (c) 1984 Nintendo */
        //TODO 	DRIVER( balonfgt )	/* (c) 1984 Nintendo */
        //TODO 	DRIVER( vsmahjng )	/* (c) 1984 Nintendo */
        //TODO 	DRIVER( vsbball )	/* (c) 1984 Nintendo */
        //TODO 	DRIVER( vsbballj )	/* (c) 1984 Nintendo */
        //TODO 	DRIVER( vsbbalja )	/* (c) 1984 Nintendo */
        //TODO 	DRIVER( iceclmrj )	/* (c) 1984 Nintendo */
        //TODO 	DRIVER( vsgshoe )	/* (c) 1986 Nintendo */
        //TODO TESTDRIVER( supxevs )
        //TODO TESTDRIVER( vsfdf )
        //TODO TESTDRIVER( smgolfb )
        //TODO TESTDRIVER( vsbbaljb )
        //TODO 
        //TODO 	/* Midway 8080 b/w games */
        //TODO 	DRIVER( seawolf )	/* 596 [1976] */
        //TODO 	DRIVER( gunfight )	/* 597 [1975] */
        //TODO 	/* 603 - Top Gun [1976] */
        //TODO 	DRIVER( tornbase )	/* 605 [1976] */
        //TODO 	DRIVER( 280zzzap )	/* 610 [1976] */
        //TODO 	DRIVER( maze )		/* 611 [1976] */
        //TODO 	DRIVER( boothill )	/* 612 [1977] */
        //TODO 	DRIVER( checkmat )	/* 615 [1977] */
        //TODO 	DRIVER( desertgu )	/* 618 [1977] */
        //TODO 	DRIVER( dplay )		/* 619 [1977] */
        //TODO 	DRIVER( lagunar )	/* 622 [1977] */
        //TODO 	DRIVER( gmissile )	/* 623 [1977] */
        //TODO 	DRIVER( m4 )		/* 626 [1977] */
        //TODO 	DRIVER( clowns )	/* 630 [1978] */
        //TODO 	DRIVER( clowns1 )	/* 630 [1978] */
        //TODO 	/* 640 - Space Walk [1978] */
        //TODO 	DRIVER( einnings )	/* 642 [1978] Midway */
        //TODO 	DRIVER( shuffle )	/* 643 [1978] */
        //TODO 	DRIVER( dogpatch )	/* 644 [1977] */
        //TODO 	DRIVER( spcenctr )	/* 645 (c) 1980 Midway */
        //TODO 	DRIVER( phantom2 )	/* 652 [1979] */
        //TODO 	DRIVER( bowler )	/* 730 [1978] Midway */
        //TODO 	DRIVER( invaders )	/* 739 [1979] */
        //TODO 	DRIVER( blueshrk )	/* 742 [1978] */
        //TODO 	DRIVER( invad2ct )	/* 851 (c) 1980 Midway */
        //TODO 	DRIVER( invadpt2 )	/* 852 [1980] Taito */
        //TODO 	DRIVER( invaddlx )	/* 852 [1980] Midway */
        //TODO 	DRIVER( moonbase )	/* Zeta - Nichibutsu */
        //TODO 	/* 870 - Space Invaders Deluxe cocktail */
        //TODO 	DRIVER( earthinv )
        //TODO 	DRIVER( spaceatt )
        //TODO 	DRIVER( spaceat2 )
        //TODO 	DRIVER( sinvzen )
        //TODO 	DRIVER( superinv )
        //TODO 	DRIVER( sstrangr )
        //TODO 	DRIVER( sstrngr2 )
        //TODO 	DRIVER( sinvemag )
        //TODO 	DRIVER( jspecter )
        //TODO 	DRIVER( jspectr2 )
        //TODO 	DRIVER( invrvnge )
        //TODO 	DRIVER( invrvnga )
        //TODO 	DRIVER( galxwars )
        //TODO 	DRIVER( galxwar2 )
        //TODO 	DRIVER( galxwart )
        //TODO 	DRIVER( starw )
        //TODO 	DRIVER( lrescue )	/* LR  (c) 1979 Taito */
        //TODO 	DRIVER( grescue )	/* bootleg? */
        //TODO 	DRIVER( desterth )	/* bootleg */
        //TODO 	DRIVER( cosmicmo )	/* Universal */
        //TODO 	DRIVER( rollingc )	/* Nichibutsu */
        //TODO 	DRIVER( sheriff )	/* (c) Nintendo */
        //TODO 	DRIVER( bandido )	/* (c) Exidy */
        //TODO 	DRIVER( ozmawars )	/* Shin Nihon Kikaku (SNK) */
        //TODO 	DRIVER( ozmawar2 )	/* Shin Nihon Kikaku (SNK) */
        //TODO 	DRIVER( solfight )	/* bootleg */
        //TODO 	DRIVER( spaceph )	/* Zilec Games */
        //TODO 	DRIVER( schaser )	/* RT  Taito */
        //TODO 	DRIVER( schasrcv )	/* RT  Taito */
        //TODO 	DRIVER( lupin3 )	/* LP  (c) 1980 Taito */
        //TODO 	DRIVER( helifire )	/* (c) Nintendo */
        //TODO 	DRIVER( helifira )	/* (c) Nintendo */
        //TODO 	DRIVER( spacefev )
        //TODO 	DRIVER( sfeverbw )
        //TODO 	DRIVER( spclaser )
        //TODO 	DRIVER( laser )
        //TODO 	DRIVER( spcewarl )
        //TODO 	DRIVER( polaris )	/* PS  (c) 1980 Taito */
        //TODO 	DRIVER( polarisa )	/* PS  (c) 1980 Taito */
        //TODO 	DRIVER( ballbomb )	/* TN  (c) 1980 Taito */
        //TODO 	DRIVER( m79amb )
        //TODO 	DRIVER( alieninv )
        //TODO 	DRIVER( sitv )
        //TODO 	DRIVER( sicv )
        //TODO 	DRIVER( sisv )
        //TODO 	DRIVER( sisv2 )
        //TODO 	DRIVER( spacewr3 )
        //TODO 	DRIVER( invaderl )
        //TODO 	DRIVER( yosakdon )
        //TODO 	DRIVER( spceking )
        //TODO 	DRIVER( spcewars )
        //TODO 
        //TODO 	/* Meadows S2650 games */
        //TODO 	DRIVER( lazercmd )	/* [1976?] */
        //TODO 	DRIVER( bbonk )		/* [1976?] */
        //TODO 	DRIVER( deadeye )	/* [1978?] */
        //TODO 	DRIVER( gypsyjug )	/* [1978?] */
        //TODO 	DRIVER( minferno )	/* [1978?] */
        //TODO 	DRIVER( medlanes )	/* [1977?] */
        //TODO 
        //TODO 	/* CVS games */
        //TODO 	DRIVER( cosmos )	/* (c) 1981 Century */
        //TODO 	DRIVER( darkwar )	/* (c) 1981 Century */
        //TODO 	DRIVER( spacefrt )	/* (c) 1981 Century */
        //TODO 	DRIVER( 8ball )		/* (c) 1982 Century */
        //TODO 	DRIVER( 8ball1 )
        //TODO 	DRIVER( logger )	/* (c) 1982 Century */
        //TODO 	DRIVER( dazzler )	/* (c) 1982 Century */
        //TODO 	DRIVER( wallst )	/* (c) 1982 Century */
        //TODO 	DRIVER( radarzon )	/* (c) 1982 Century */
        //TODO 	DRIVER( radarzn1 )	/* (c) 1982 Century */
        //TODO 	DRIVER( radarznt )	/* (c) 1982 Tuni Electro Service */
        //TODO 	DRIVER( outline )	/* (c) 1982 Century */
        //TODO 	DRIVER( goldbug )	/* (c) 1982 Century */
        //TODO 	DRIVER( heartatk )	/* (c) 1983 Century Electronics */
        //TODO 	DRIVER( hunchbak )	/* (c) 1983 Century */
        //TODO 	DRIVER( superbik )	/* (c) 1983 Century */
        //TODO 	DRIVER( hero )		/* (c) 1983 Seatongrove (c) 1984 CVS */
        //TODO 	DRIVER( huncholy )	/* (c) 1984 Seatongrove (c) CVS */
        //TODO 
        //TODO 	/* Midway "Astrocade" games */
        //TODO 	DRIVER( seawolf2 )
        //TODO 	DRIVER( spacezap )	/* (c) 1980 */
        //TODO 	DRIVER( ebases )
        //TODO 	DRIVER( wow )		/* (c) 1980 */
        //TODO 	DRIVER( gorf )		/* (c) 1981 */
        //TODO 	DRIVER( gorfpgm1 )	/* (c) 1981 */
        //TODO 	DRIVER( robby )		/* (c) 1981 Bally Midway */
        //TODO TESTDRIVER( profpac )	/* (c) 1983 Bally Midway */
        //TODO 
        //TODO 	/* Bally Midway MCR games */
        //TODO 	/* MCR1 */
        //TODO 	DRIVER( solarfox )	/* (c) 1981 */
        //TODO 	DRIVER( kick )		/* (c) 1981 */
        //TODO 	DRIVER( kicka )		/* bootleg? */
        //TODO 	/* MCR2 */
        //TODO 	DRIVER( shollow )	/* (c) 1981 */
        //TODO 	DRIVER( shollow2 )	/* (c) 1981 */
        //TODO 	DRIVER( tron )		/* (c) 1982 */
        //TODO 	DRIVER( tron2 )		/* (c) 1982 */
        //TODO 	DRIVER( kroozr )	/* (c) 1982 */
        //TODO 	DRIVER( domino )	/* (c) 1982 */
        //TODO 	DRIVER( wacko )		/* (c) 1982 */
        //TODO 	DRIVER( twotiger )	/* (c) 1984 */
        //TODO 	DRIVER( twotigra )	/* (c) 1984 */
        //TODO 	/* MCR2 + MCR3 sprites */
        //TODO 	DRIVER( journey )	/* (c) 1983 */
        //TODO 	/* MCR3 */
        //TODO 	DRIVER( tapper )	/* (c) 1983 */
        //TODO 	DRIVER( tappera )	/* (c) 1983 */
        //TODO 	DRIVER( sutapper )	/* (c) 1983 */
        //TODO 	DRIVER( rbtapper )	/* (c) 1984 */
        //TODO 	DRIVER( timber )	/* (c) 1984 */
        //TODO 	DRIVER( dotron )	/* (c) 1983 */
        //TODO 	DRIVER( dotrona )	/* (c) 1983 */
        //TODO 	DRIVER( dotrone )	/* (c) 1983 */
        //TODO 	DRIVER( demoderb )	/* (c) 1984 */
        //TODO 	DRIVER( demoderm )	/* (c) 1984 */
        //TODO 	DRIVER( sarge )		/* (c) 1985 */
        //TODO 	DRIVER( rampage )	/* (c) 1986 */
        //TODO 	DRIVER( rampage2 )	/* (c) 1986 */
        //TODO 	DRIVER( powerdrv )	/* (c) 1986 */
        //TODO 	DRIVER( stargrds )	/* (c) 1987 */
        //TODO 	DRIVER( maxrpm )	/* (c) 1986 */
        //TODO 	DRIVER( spyhunt )	/* (c) 1983 */
        //TODO 	DRIVER( turbotag )	/* (c) 1985 */
        //TODO 	DRIVER( crater )	/* (c) 1984 */
        //TODO 	/* MCR 68000 */
        //TODO 	DRIVER( zwackery )	/* (c) 1984 */
        //TODO 	DRIVER( xenophob )	/* (c) 1987 */
        //TODO 	DRIVER( spyhunt2 )	/* (c) 1987 */
        //TODO 	DRIVER( spyhnt2a )	/* (c) 1987 */
        //TODO 	DRIVER( blasted )	/* (c) 1988 */
        //TODO 	DRIVER( archrivl )	/* (c) 1989 */
        //TODO 	DRIVER( archriv2 )	/* (c) 1989 */
        //TODO 	DRIVER( trisport )	/* (c) 1989 */
        //TODO 	DRIVER( pigskin )	/* (c) 1990 */
        //TODO 
        //TODO 	/* Bally / Sente games */
        //TODO 	DRIVER( sentetst )
        //TODO 	DRIVER( cshift )	/* (c) 1984 */
        //TODO 	DRIVER( gghost )	/* (c) 1984 */
        //TODO 	DRIVER( hattrick )	/* (c) 1984 */
        //TODO 	DRIVER( otwalls )	/* (c) 1984 */
        //TODO 	DRIVER( snakepit )	/* (c) 1984 */
        //TODO 	DRIVER( snakjack )	/* (c) 1984 */
        //TODO 	DRIVER( stocker )	/* (c) 1984 */
        //TODO 	DRIVER( triviag1 )	/* (c) 1984 */
        //TODO 	DRIVER( triviag2 )	/* (c) 1984 */
        //TODO 	DRIVER( triviasp )	/* (c) 1984 */
        //TODO 	DRIVER( triviayp )	/* (c) 1984 */
        //TODO 	DRIVER( triviabb )	/* (c) 1984 */
        //TODO 	DRIVER( gimeabrk )	/* (c) 1985 */
        //TODO 	DRIVER( minigolf )	/* (c) 1985 */
        //TODO 	DRIVER( minigol2 )	/* (c) 1985 */
        //TODO 	DRIVER( toggle )	/* (c) 1985 */
        //TODO 	DRIVER( nametune )	/* (c) 1986 */
        //TODO 	DRIVER( nstocker )	/* (c) 1986 */
        //TODO 	DRIVER( sfootbal )	/* (c) 1986 */
        //TODO 	DRIVER( spiker )	/* (c) 1986 */
        //TODO 	DRIVER( stompin )	/* (c) 1986 */
        //TODO 	DRIVER( rescraid )	/* (c) 1987 */
        //TODO 	DRIVER( rescrdsa )	/* (c) 1987 */
        //TODO 	DRIVER( grudge )
        //TODO 	DRIVER( shrike )	/* (c) 1987 */
        //TODO 	DRIVER( gridlee )	/* [1983 Videa] prototype - no copyright notice */
        //TODO 
        //TODO 	/* Irem games */
        //TODO 	/* trivia: IREM means "International Rental Electronics Machines" */
        //TODO 	DRIVER( ipminvad )	/* M10 no copyright notice */
        //TODO 	DRIVER( skychut )	/* Irem [1980] */
        //TODO 	DRIVER( spacbeam )	/* M15 no copyright notice */
        //TODO 	DRIVER( greenber )	/* Irem */
        //TODO 
        //TODO 	DRIVER( redalert )	/* (c) 1981 + "GDI presents" */
        //TODO 	DRIVER( olibochu )	/* M47 (c) 1981 + "GDI presents" */
        //TODO 	DRIVER( mpatrol )	/* M52 (c) 1982 */
        //TODO 	DRIVER( mpatrolw )	/* M52 (c) 1982 + Williams license */
        //TODO 	DRIVER( troangel )	/* (c) 1983 */
        //TODO 	DRIVER( yard )		/* (c) 1983 */
        //TODO 	DRIVER( vsyard )	/* (c) 1983/1984 */
        //TODO 	DRIVER( vsyard2 )	/* (c) 1983/1984 */
        //TODO 	DRIVER( travrusa )	/* (c) 1983 */
        //TODO 	DRIVER( motorace )	/* (c) 1983 Williams license */
        //TODO 	DRIVER( shtrider )	/* (c) 1984 Seibu Kaihatsu */
        //TODO 	DRIVER( wilytowr )	/* M63 (c) 1984 */
        //TODO 	DRIVER( atomboy )	/* M63 (c) 1985 Irem + Memetron license */
        //TODO 	/* M62 */
        //TODO 	DRIVER( kungfum )	/* (c) 1984 */
        //TODO 	DRIVER( kungfud )	/* (c) 1984 + Data East license */
        //TODO 	DRIVER( spartanx )	/* (c) 1984 (Japan) */
        //TODO 	DRIVER( kungfub )	/* bootleg */
        //TODO 	DRIVER( kungfub2 )	/* bootleg */
        //TODO 	DRIVER( battroad )	/* (c) 1984 */
        //TODO 	DRIVER( ldrun )		/* (c) 1984 licensed from Broderbund */
        //TODO 	DRIVER( ldruna )	/* (c) 1984 licensed from Broderbund */
        //TODO 	DRIVER( ldrun2 )	/* (c) 1984 licensed from Broderbund */
        //TODO 	DRIVER( ldrun3 )	/* (c) 1985 licensed from Broderbund */
        //TODO 	DRIVER( ldrun4 )	/* (c) 1986 licensed from Broderbund */
        //TODO 	DRIVER( lotlot )	/* (c) 1985 licensed from Tokuma Shoten */
        //TODO 	DRIVER( kidniki )	/* (c) 1986 + Data East USA license */
        //TODO 	DRIVER( yanchamr )	/* (c) 1986 (Japan) */
        //TODO 	DRIVER( spelunkr )	/* (c) 1985 licensed from Broderbund */
        //TODO 	DRIVER( spelnkrj )	/* (c) 1985 licensed from Broderbund */
        //TODO 	DRIVER( spelunk2 )	/* (c) 1986 licensed from Broderbund */
        //TODO 	DRIVER( youjyudn )	/* (c) 1986 (Japan) */
        //TODO 
        //TODO 	DRIVER( vigilant )	/* (c) 1988 (World) */
        //TODO 	DRIVER( vigilntu )	/* (c) 1988 (US) */
        //TODO 	DRIVER( vigilntj )	/* (c) 1988 (Japan) */
        //TODO 	DRIVER( kikcubic )	/* (c) 1988 (Japan) */
        //TODO 	/* M72 (and derivatives) */
        //TODO 	DRIVER( rtype )		/* (c) 1987 (Japan) */
        //TODO 	DRIVER( rtypepj )	/* (c) 1987 (Japan) */
        //TODO 	DRIVER( rtypeu )	/* (c) 1987 + Nintendo USA license (US) */
        //TODO 	DRIVER( bchopper )	/* (c) 1987 */
        //TODO 	DRIVER( mrheli )	/* (c) 1987 (Japan) */
        //TODO 	DRIVER( nspirit )	/* (c) 1988 */
        //TODO 	DRIVER( nspiritj )	/* (c) 1988 (Japan) */
        //TODO 	DRIVER( imgfight )	/* (c) 1988 (Japan) */
        //TODO 	DRIVER( loht )		/* (c) 1989 */
        //TODO 	DRIVER( xmultipl )	/* (c) 1989 (Japan) */
        //TODO 	DRIVER( dbreed )	/* (c) 1989 */
        //TODO 	DRIVER( rtype2 )	/* (c) 1989 */
        //TODO 	DRIVER( rtype2j )	/* (c) 1989 (Japan) */
        //TODO 	DRIVER( majtitle )	/* (c) 1990 (Japan) */
        //TODO 	DRIVER( hharry )	/* (c) 1990 (World) */
        //TODO 	DRIVER( hharryu )	/* (c) 1990 Irem America (US) */
        //TODO 	DRIVER( dkgensan )	/* (c) 1990 (Japan) */
        //TODO 	DRIVER( dkgenm72 )	/* (c) 1990 (Japan) */
        //TODO 	DRIVER( poundfor )	/* (c) 1990 (World) */
        //TODO 	DRIVER( poundfou )	/* (c) 1990 Irem America (US) */
        //TODO 	DRIVER( airduel )	/* (c) 1990 (Japan) */
        //TODO 	DRIVER( cosmccop )	/* (c) 1991 (World) */
        //TODO 	DRIVER( gallop )	/* (c) 1991 (Japan) */
        //TODO 	DRIVER( kengo )		/* (c) 1991 */
        //TODO 	/* not M72, but same sound hardware */
        //TODO 	DRIVER( sichuan2 )	/* (c) 1989 Tamtex */
        //TODO 	DRIVER( sichuana )	/* (c) 1989 Tamtex */
        //TODO 	DRIVER( shisen )	/* (c) 1989 Tamtex */
        //TODO 	DRIVER( matchit )	/* (c) 1989 Tamtex */
        //TODO 	/* M90 */
        //TODO 	DRIVER( hasamu )	/* (c) 1991 Irem (Japan) */
        //TODO 	DRIVER( bombrman )	/* (c) 1991 Irem (Japan) */
        //TODO TESTDRIVER( dynablsb )	/* bootleg */
        //TODO 	/* M97 */
        //TODO 	DRIVER( bbmanw )	/* (c) 1992 Irem (World) */
        //TODO 	DRIVER( bbmanwj )	/* (c) 1992 Irem (Japan) */
        //TODO 	DRIVER( atompunk )	/* (c) 1992 Irem America (US) */
        //TODO 	DRIVER( quizf1 )	/* (c) 1992 Irem (Japan) */
        //TODO TESTDRIVER( riskchal )
        //TODO TESTDRIVER( gussun )
        //TODO TESTDRIVER( shisen2 )
        //TODO 	/* M92 */
        //TODO 	DRIVER( gunforce )	/* (c) 1991 Irem (World) */
        //TODO 	DRIVER( gunforcu )	/* (c) 1991 Irem America (US) */
        //TODO 	DRIVER( gunforcj )	/* (c) 1991 Irem (Japan) */
        //TODO 	DRIVER( bmaster )	/* (c) 1991 Irem */
        //TODO 	DRIVER( lethalth )	/* (c) 1991 Irem (World) */
        //TODO 	DRIVER( thndblst )	/* (c) 1991 Irem (Japan) */
        //TODO 	DRIVER( uccops )	/* (c) 1992 Irem (World) */
        //TODO 	DRIVER( uccopsj )	/* (c) 1992 Irem (Japan) */
        //TODO 	DRIVER( mysticri )	/* (c) 1992 Irem (World) */
        //TODO 	DRIVER( gunhohki )	/* (c) 1992 Irem (Japan) */
        //TODO 	DRIVER( majtitl2 )	/* (c) 1992 Irem (World) */
        //TODO 	DRIVER( skingame )	/* (c) 1992 Irem America (US) */
        //TODO 	DRIVER( skingam2 )	/* (c) 1992 Irem America (US) */
        //TODO 	DRIVER( hook )		/* (c) 1992 Irem (World) */
        //TODO 	DRIVER( hooku )		/* (c) 1992 Irem America (US) */
        //TODO 	DRIVER( rtypeleo )	/* (c) 1992 Irem (World) */
        //TODO 	DRIVER( rtypelej )	/* (c) 1992 Irem (Japan) */
        //TODO 	DRIVER( inthunt )	/* (c) 1993 Irem (World) */
        //TODO 	DRIVER( inthuntu )	/* (c) 1993 Irem (US) */
        //TODO 	DRIVER( kaiteids )	/* (c) 1993 Irem (Japan) */
        //TODO 	DRIVER( nbbatman )	/* (c) 1993 Irem America (US) */
        //TODO 	DRIVER( leaguemn )	/* (c) 1993 Irem (Japan) */
        //TODO 	DRIVER( psoldier )	/* (c) 1993 Irem (Japan) */
        //TODO 	DRIVER( dsccr94j )	/* (c) 1994 Irem (Japan) */
        //TODO 	DRIVER( gunforc2 )	/* (c) 1994 Irem */
        //TODO 	DRIVER( geostorm )	/* (c) 1994 Irem (Japan) */
        //TODO 	/* M107 */
        //TODO 	DRIVER( firebarr )	/* (c) 1993 Irem (Japan) */
        //TODO 	DRIVER( dsoccr94 )	/* (c) 1994 Irem (Data East Corporation license) */
        //TODO TESTDRIVER( wpksoc )
        //TODO 
        //TODO 	/* Gottlieb/Mylstar games (Gottlieb became Mylstar in 1983) */
        //TODO 	DRIVER( reactor )	/* GV-100 (c) 1982 Gottlieb */
        //TODO 	DRIVER( mplanets )	/* GV-102 (c) 1983 Gottlieb */
        //TODO 	DRIVER( qbert )		/* GV-103 (c) 1982 Gottlieb */
        //TODO 	DRIVER( qbertjp )	/* GV-103 (c) 1982 Gottlieb + Konami license */
        //TODO 	DRIVER( insector )	/* GV-??? (c) 1982 Gottlieb - never released */
        //TODO 	DRIVER( krull )		/* GV-105 (c) 1983 Gottlieb */
        //TODO 	DRIVER( sqbert )	/* GV-??? (c) 1983 Mylstar - never released */
        //TODO 	DRIVER( mach3 )		/* GV-109 (c) 1983 Mylstar */
        //TODO 	DRIVER( usvsthem )	/* GV-??? (c) 198? Mylstar */
        //TODO 	DRIVER( 3stooges )	/* GV-113 (c) 1984 Mylstar */
        //TODO 	DRIVER( qbertqub )	/* GV-119 (c) 1983 Mylstar */
        //TODO 	DRIVER( screwloo )	/* GV-123 (c) 1983 Mylstar - never released */
        //TODO 	DRIVER( curvebal )	/* GV-134 (c) 1984 Mylstar */
        //TODO 
        //TODO 	/* Taito "Qix hardware" games */
        //TODO 	DRIVER( qix )		/* LK  (c) 1981 Taito America Corporation */
        //TODO 	DRIVER( qixa )		/* LK  (c) 1981 Taito America Corporation */
        //TODO 	DRIVER( qixb )		/* LK  (c) 1981 Taito America Corporation */
        //TODO 	DRIVER( qix2 )		/* ??  (c) 1981 Taito America Corporation */
        //TODO 	DRIVER( sdungeon )	/* SD  (c) 1981 Taito America Corporation */
        //TODO 	DRIVER( elecyoyo )	/* YY  (c) 1982 Taito America Corporation */
        //TODO 	DRIVER( elecyoy2 )	/* YY  (c) 1982 Taito America Corporation */
        //TODO 	DRIVER( kram )		/* KS  (c) 1982 Taito America Corporation */
        //TODO 	DRIVER( kram2 )		/* KS  (c) 1982 Taito America Corporation */
        //TODO 	DRIVER( kram3 )
        //TODO 	DRIVER( zookeep )	/* ZA  (c) 1982 Taito America Corporation */
        //TODO 	DRIVER( zookeep2 )	/* ZA  (c) 1982 Taito America Corporation */
        //TODO 	DRIVER( zookeep3 )	/* ZA  (c) 1982 Taito America Corporation */
        //TODO 	DRIVER( slither )	/* (c) 1982 Century II */
        //TODO 	DRIVER( slithera )	/* (c) 1982 Century II */
        //TODO 	DRIVER( complexx )	/* ??  (c) 1984 Taito America Corporation */
        //TODO 
        //TODO 	/* Taito SJ System games */
        //TODO 	DRIVER( spaceskr )	/* EB  (c) 1981 Taito Corporation */
        //TODO 	DRIVER( junglek )	/* KN  (c) 1982 Taito Corporation */
        //TODO 	DRIVER( junglkj2 )	/* KN  (c) 1982 Taito Corporation */
        //TODO 	DRIVER( jungleh )	/* KN  (c) 1982 Taito America Corporation */
        //TODO 	DRIVER( junglhbr )	/* KN  (c) 1982 Taito do Brasil */
        //TODO 	DRIVER( piratpet )	/* KN  (c) 1982 Taito America Corporation */
        //TODO 	DRIVER( alpine )	/* RH  (c) 1982 Taito Corporation */
        //TODO 	DRIVER( alpinea )	/* RH  (c) 1982 Taito Corporation */
        //TODO 	DRIVER( timetunl )	/* UN  (c) 1982 Taito Corporation */
        //TODO 	DRIVER( wwestern )	/* WW  (c) 1982 Taito Corporation */
        //TODO 	DRIVER( wwester1 )	/* WW  (c) 1982 Taito Corporation */
        //TODO 	DRIVER( frontlin )	/* FL  (c) 1982 Taito Corporation */
        //TODO 	DRIVER( elevator )	/* EA  (c) 1983 Taito Corporation */
        //TODO 	DRIVER( elevatob )	/* bootleg */
        //TODO 	DRIVER( tinstar )	/* A10 (c) 1983 Taito Corporation */
        //TODO 	DRIVER( waterski )	/* A03 (c) 1983 Taito Corporation */
        //TODO 	DRIVER( bioatack )	/* AA8 (c) 1983 Taito Corporation + Fox Video Games license */
        //TODO 	DRIVER( hwrace )	/* AC4 (c) 1983 Taito Corporation */
        //TODO 	DRIVER( sfposeid )	/* A14 (c) 1984 Taito Corporation */
        //TODO TESTDRIVER( kikstart )	/* A20 */
        //TODO 
        //TODO 	/* other Taito games */
        //TODO 	DRIVER( crbaloon )	/* CL  (c) 1980 Taito Corporation */
        //TODO 	DRIVER( crbalon2 )	/* CL  (c) 1980 Taito Corporation */
        //TODO 	DRIVER( grchamp )	/* GM  (c) 1981 Taito Corporation */
        //TODO 	DRIVER( bking )		/* DM  (c) 1982 Taito Corporation */
        //TODO 	DRIVER( bking2 )	/* AD6 (c) 1983 Taito Corporation */
        //TODO TESTDRIVER( josvolly )	/* ??? (c) 1983 Taito Corporation */
        //TODO 	DRIVER( gsword )	/* ??? (c) 1984 Taito Corporation */
        //TODO 	DRIVER( lkage )		/* A54 (c) 1984 Taito Corporation */
        //TODO 	DRIVER( lkageb )	/* bootleg */
        //TODO 	DRIVER( lkageb2 )	/* bootleg */
        //TODO 	DRIVER( lkageb3 )	/* bootleg */
        //TODO TESTDRIVER( msisaac )	/* A34 (c) 1985 Taito Corporation */
        //TODO 	DRIVER( retofinv )	/* A37 (c) 1985 Taito Corporation */
        //TODO 	DRIVER( retofin1 )	/* bootleg */
        //TODO 	DRIVER( retofin2 )	/* bootleg */
        //TODO 	DRIVER( fightrol )	/* (c) 1983 Taito */
        //TODO 	DRIVER( rollrace )	/* (c) 1983 Williams */
        //TODO 	DRIVER( vsgongf )	/* (c) 1984 Kaneko */
        //TODO 	DRIVER( undoukai )	/* A17 (c) 1984 Taito */
        //TODO 	DRIVER( 40love )	/* A30 (c) 1984 Taito */
        //TODO 	DRIVER( tsamurai )	/* A35 (c) 1985 Taito */
        //TODO 	DRIVER( tsamura2 )	/* A35 (c) 1985 Taito */
        //TODO 	DRIVER( nunchaku )	/* ??? (c) 1985 Taito */
        //TODO 	DRIVER( yamagchi )	/* A38 (c) 1985 Taito */
        //TODO 	DRIVER( m660 )      /* ??? (c) 1986 Taito America Corporation */
        //TODO 	DRIVER( m660j )     /* ??? (c) 1986 Taito Corporation (Japan) */
        //TODO 	DRIVER( m660b )     /* bootleg */
        //TODO 	DRIVER( alphaxz )   /* ??? (c) 1986 Ed/Wood Place */
        //TODO 	DRIVER( buggychl )	/* A22 (c) 1984 Taito Corporation */
        //TODO 	DRIVER( buggycht )	/* A22 (c) 1984 Taito Corporation + Tefri license */
        //TODO 	DRIVER( flstory )	/* A45 (c) 1985 Taito Corporation */
        //TODO 	DRIVER( flstoryj )	/* A45 (c) 1985 Taito Corporation (Japan) */
        //TODO 	DRIVER( onna34ro )	/* A52 (c) 1985 Taito Corporation (Japan) */
        //TODO 	DRIVER( onna34ra )	/* A52 (c) 1985 Taito Corporation (Japan) */
        //TODO 	DRIVER( gladiatr )	/* ??? (c) 1986 Taito America Corporation (US) */
        //TODO 	DRIVER( ogonsiro )	/* ??? (c) 1986 Taito Corporation (Japan) */
        //TODO 	DRIVER( nycaptor )	/* A50 (c) 1985 Taito Corporation */
        //TODO TESTDRIVER( cyclshtg )	/* A97 (c) 1986 Taito Corporation */
        //TODO 	DRIVER( benberob )	/* A26 */
        //TODO 	DRIVER( halleys )	/* A62 (c) 1986 Taito America Corporation + Coin It (US) */
        //TODO 	DRIVER( halleysc )	/* A62 (c) 1986 Taito Corporation (Japan) */
        //TODO 	DRIVER( halleycj )	/* A62 (c) 1986 Taito Corporation (Japan) */
        //TODO 	DRIVER( lsasquad )	/* A64 (c) 1986 Taito Corporation / Taito America (dip switch) */
        //TODO 	DRIVER( storming )	/* A64 (c) 1986 Taito Corporation */
        //TODO 	DRIVER( tokio )		/* A71 1986 */
        //TODO 	DRIVER( tokiob )	/* bootleg */
        //TODO 	DRIVER( bublbobl )	/* A78 (c) 1986 Taito Corporation */
        //TODO 	DRIVER( bublbobr )	/* A78 (c) 1986 Taito America Corporation + Romstar license */
        //TODO 	DRIVER( bubbobr1 )	/* A78 (c) 1986 Taito America Corporation + Romstar license */
        //TODO 	DRIVER( boblbobl )	/* bootleg */
        //TODO 	DRIVER( sboblbob )	/* bootleg */
        //TODO 	DRIVER( missb2 )	/* bootleg on enhanced hardware */
        //TODO 	DRIVER( kikikai )	/* A85 (c) 1986 Taito Corporation */
        //TODO 	DRIVER( kicknrun )	/* A87 (c) 1986 Taito Corporation */
        //TODO 	DRIVER( mexico86 )	/* bootleg (Micro Research) */
        //TODO 	DRIVER( darius )	/* A96 (c) 1986 Taito Corporation Japan (World) */
        //TODO 	DRIVER( dariusj )	/* A96 (c) 1986 Taito Corporation (Japan) */
        //TODO 	DRIVER( dariuso )	/* A96 (c) 1986 Taito Corporation (Japan) */
        //TODO 	DRIVER( dariuse )	/* A96 (c) 1986 Taito Corporation (Japan) */
        //TODO 	DRIVER( rastan )	/* B04 (c) 1987 Taito Corporation Japan (World) */
        //TODO 	DRIVER( rastanu )	/* B04 (c) 1987 Taito America Corporation (US) */
        //TODO 	DRIVER( rastanu2 )	/* B04 (c) 1987 Taito America Corporation (US) */
        //TODO 	DRIVER( rastsaga )	/* B04 (c) 1987 Taito Corporation (Japan)*/
        //TODO 	DRIVER( topspeed )	/* B14 (c) 1987 Taito Corporation Japan (World) */
        //TODO 	DRIVER( topspedu )	/* B14 (c) 1987 Taito America Corporation (US) */
        //TODO 	DRIVER( fullthrl )	/* B14 (c) 1987 Taito Corporation (Japan) */
        //TODO 	DRIVER( opwolf )	/* B20 (c) 1987 Taito America Corporation (US) */
        //TODO 	DRIVER( opwolfb )	/* bootleg */
        //TODO 	DRIVER( othunder )	/* B67 (c) 1988 Taito Corporation Japan (World) */
        //TODO 	DRIVER( othundu )	/* B67 (c) 1988 Taito America Corporation (US) */
        //TODO 	DRIVER( rainbow )	/* B22 (c) 1987 Taito Corporation */
        //TODO 	DRIVER( rainbowo )	/* B22 (c) 1987 Taito Corporation */
        //TODO 	DRIVER( rainbowe )	/* B39 (c) 1988 Taito Corporation */
        //TODO 	DRIVER( jumping )	/* bootleg */
        //TODO 	DRIVER( arkanoid )	/* A75 (c) 1986 Taito Corporation Japan (World) */
        //TODO 	DRIVER( arknoidu )	/* A75 (c) 1986 Taito America Corporation + Romstar license (US) */
        //TODO 	DRIVER( arknoidj )	/* A75 (c) 1986 Taito Corporation (Japan) */
        //TODO 	DRIVER( arkbl2 )	/* bootleg */
        //TODO 	DRIVER( arkbl3 )	/* bootleg */
        //TODO 	DRIVER( paddle2 )	/* bootleg */
        //TODO 	DRIVER( arkatayt )	/* bootleg */
        //TODO 	DRIVER( arkblock )	/* bootleg */
        //TODO 	DRIVER( arkbloc2 )	/* bootleg */
        //TODO 	DRIVER( arkangc )	/* bootleg */
        //TODO 	DRIVER( arkatour )	/* ??? (c) 1987 Taito America Corporation + Romstar license (US) */
        //TODO 	DRIVER( superqix )	/* B03 1987 */
        //TODO 	DRIVER( sqixbl )	/* bootleg? but (c) 1987 */
        //TODO 	DRIVER( perestro )	/* (c) 1993 Promat / Fuuki */
        //TODO 	DRIVER( exzisus )	/* B23 (c) 1987 Taito Corporation (Japan) */     
        //TODO 	DRIVER( volfied )	/* C04 (c) 1989 Taito Corporation Japan (World) */
        //TODO 	DRIVER( volfiedu )	/* C04 (c) 1989 Taito America Corporation (US) */
        //TODO 	DRIVER( volfiedj )	/* C04 (c) 1989 Taito Corporation (Japan) */
        //TODO 	DRIVER( bonzeadv )	/* B41 (c) 1988 Taito Corporation Japan (World) */
        //TODO 	DRIVER( bonzeadu )	/* B41 (c) 1988 Taito America Corporation (US) */
        //TODO 	DRIVER( jigkmgri )	/* B41 (c) 1988 Taito Corporation (Japan)*/
        //TODO 	DRIVER( asuka )		/* ??? (c) 1988 Taito Corporation (Japan) */
        //TODO 	DRIVER( mofflott )	/* C17 (c) 1989 Taito Corporation (Japan) */
        //TODO 	DRIVER( cadash )	/* C21 (c) 1989 Taito Corporation Japan */
        //TODO 	DRIVER( cadashj )	/* C21 (c) 1989 Taito Corporation */
        //TODO 	DRIVER( cadashu )	/* C21 (c) 1989 Taito America Corporation */
        //TODO 	DRIVER( cadashi )	/* C21 (c) 1989 Taito Corporation Japan */
        //TODO 	DRIVER( cadashf )	/* C21 (c) 1989 Taito Corporation Japan */
        //TODO 	DRIVER( galmedes )	/* (c) 1992 Visco (Japan) */
        //TODO 	DRIVER( earthjkr )	/* (c) 1993 Visco (Japan) */
        //TODO 	DRIVER( eto )		/* (c) 1994 Visco (Japan) */
        //TODO 	DRIVER( wgp )		/* C32 (c) 1989 Taito America Corporation (US) */
        //TODO 	DRIVER( wgpj )		/* C32 (c) 1989 Taito Corporation (Japan) */
        //TODO 	DRIVER( wgpjoy )	/* C32 (c) 1989 Taito Corporation (Japan) */
        //TODO 	DRIVER( wgpjoya )	/* C32 (c) 1989 Taito Corporation (Japan) */
        //TODO 	DRIVER( wgp2 )		/* C73 (c) 1990 Taito Corporation (Japan) */
        //TODO 	DRIVER( slapshot )	/* D71 (c) 1994 Taito Corporation (Japan) */
        //TODO 
        //TODO 	/* Taito multi-screen games */
        //TODO 	DRIVER( ninjaw )	/* B31 (c) 1987 Taito Corporation Japan (World) */
        //TODO 	DRIVER( ninjawj )	/* B31 (c) 1987 Taito Corporation (Japan) */
        //TODO 	DRIVER( darius2 )	/* C07 (c) 1989 Taito Corporation (Japan) */
        //TODO 	DRIVER( darius2d )	/* C07 (c) 1989 Taito Corporation (Japan) */
        //TODO 	DRIVER( drius2do )	/* C07 (c) 1989 Taito Corporation (Japan) */
        //TODO 	DRIVER( warriorb )	/* D24 (c) 1991 Taito Corporation (Japan) */
        //TODO 
        //TODO 	/* Taito "X"-system games */
        //TODO 	DRIVER( superman )	/* B61 (c) 1988 Taito Corporation */
        //TODO 	DRIVER( twinhawk )	/* B87 (c) 1989 Taito Corporation Japan (World) */
        //TODO 	DRIVER( twinhwku )	/* B87 (c) 1989 Taito America Corporation (US) */
        //TODO 	DRIVER( daisenpu )	/* B87 (c) 1989 Taito Corporation (Japan) */
        //TODO 	DRIVER( gigandes )	/* (c) 1989 East Technology */
        //TODO 	DRIVER( ballbros )	/* no copyright notice */
        //TODO 
        //TODO 	/* Taito "tnzs" hardware */
        //TODO 	DRIVER( plumppop )	/* A98 (c) 1987 Taito Corporation (Japan) */
        //TODO 	DRIVER( extrmatn )	/* B06 (c) 1987 World Games */
        //TODO 	DRIVER( arknoid2 )	/* B08 (c) 1987 Taito Corporation Japan (World) */
        //TODO 	DRIVER( arknid2u )	/* B08 (c) 1987 Taito America Corporation + Romstar license (US) */
        //TODO 	DRIVER( arknid2j )	/* B08 (c) 1987 Taito Corporation (Japan) */
        //TODO 	DRIVER( drtoppel )	/* B19 (c) 1987 Taito Corporation (Japan) */
        //TODO 	DRIVER( kageki )	/* B35 (c) 1988 Taito America Corporation + Romstar license (US) */
        //TODO 	DRIVER( kagekij )	/* B35 (c) 1988 Taito Corporation (Japan) */
        //TODO 	DRIVER( chukatai )	/* B44 (c) 1988 Taito Corporation (Japan) */
        //TODO 	DRIVER( tnzs )		/* B53?(c) 1988 Taito Corporation (Japan) (new logo) */
        //TODO 	DRIVER( tnzsb )		/* bootleg but Taito Corporation Japan (World) (new logo) */
        //TODO 	DRIVER( tnzs2 )		/* B53?(c) 1988 Taito Corporation Japan (World) (old logo) */
        //TODO 	DRIVER( insectx )	/* B97 (c) 1989 Taito Corporation Japan (World) */
        //TODO 
        //TODO 	/* Taito L-System games */
        //TODO 	DRIVER( raimais )	/* B36 (c) 1988 Taito Corporation (Japan) */
        //TODO 	DRIVER( kurikint )	/* B42 (c) 1988 Taito Corporation Japan (World) */
        //TODO 	DRIVER( kurikinu )	/* B42 (c) 1988 Taito America Corporation (US) */
        //TODO 	DRIVER( kurikinj )	/* B42 (c) 1988 Taito Corporation (Japan) */
        //TODO 	DRIVER( kurikina )	/* B42 (c) 1988 Taito Corporation Japan (World) */
        //TODO 	DRIVER( fhawk )		/* B70 (c) 1988 Taito Corporation (Japan) */
        //TODO 	DRIVER( plotting )	/* B96 (c) 1989 Taito Corporation Japan (World) */
        //TODO 	DRIVER( champwr )	/* C01 (c) 1989 Taito Corporation Japan (World) */
        //TODO 	DRIVER( champwru )	/* C01 (c) 1989 Taito America Corporation (US) */
        //TODO 	DRIVER( champwrj )	/* C01 (c) 1989 Taito Corporation (Japan) */
        //TODO 	DRIVER( puzznic )	/* C20 (c) 1989 Taito Corporation (Japan) */
        //TODO 	DRIVER( horshoes )	/* C47 (c) 1990 Taito America Corporation (US) */
        //TODO 	DRIVER( palamed )	/* C63 (c) 1990 Taito Corporation (Japan) */
        //TODO 	DRIVER( cachat )	/* ??? (c) 1993 Taito Corporation (Japan) */
        //TODO 	DRIVER( tubeit )	/* ??? no copyright message */
        //TODO 	DRIVER( cubybop )	/* ??? no copyright message */
        //TODO 	DRIVER( plgirls )	/* (c) 1992 Hot-B. */
        //TODO 	DRIVER( plgirls2 )	/* (c) 1993 Hot-B. */
        //TODO 
        //TODO 	/* Taito H-System games */
        //TODO 	DRIVER( syvalion )	/* B51 (c) 1988 Taito Corporation (Japan) */
        //TODO 	DRIVER( recordbr )	/* B56 (c) 1988 Taito Corporation Japan (World) */
        //TODO 	DRIVER( dleague )	/* C02 (c) 1990 Taito Corporation (Japan) */
        //TODO 
        //TODO 	/* Taito B-System games */
        //TODO 	DRIVER( masterw )	/* B72 (c) 1989 Taito Corporation Japan (World) */
        //TODO 	DRIVER( nastar )	/* B81 (c) 1988 Taito Corporation Japan (World) */
        //TODO 	DRIVER( nastarw )	/* B81 (c) 1988 Taito America Corporation (US) */
        //TODO 	DRIVER( rastsag2 )	/* B81 (c) 1988 Taito Corporation (Japan) */
        //TODO 	DRIVER( rambo3 )	/* B93 (c) 1989 Taito Europe Corporation (Europe) */
        //TODO 	DRIVER( rambo3ae )	/* B93 (c) 1989 Taito Europe Corporation (Europe) */
        //TODO 	DRIVER( rambo3a )	/* B93 (c) 1989 Taito America Corporation (US) */
        //TODO 	DRIVER( crimec )	/* B99 (c) 1989 Taito Corporation Japan (World) */
        //TODO 	DRIVER( crimecu )	/* B99 (c) 1989 Taito America Corporation (US) */
        //TODO 	DRIVER( crimecj )	/* B99 (c) 1989 Taito Corporation (Japan) */
        //TODO 	DRIVER( tetrist )	/* C12 (c) 1989 Sega Enterprises,Ltd. (Japan) */
        //TODO 	DRIVER( viofight )	/* C16 (c) 1989 Taito Corporation Japan (World) */
        //TODO 	DRIVER( ashura )	/* C43 (c) 1990 Taito Corporation (Japan) */
        //TODO 	DRIVER( ashurau )	/* C43 (c) 1990 Taito America Corporation (US) */
        //TODO 	DRIVER( hitice )	/* C59 (c) 1990 Williams (US) */
        //TODO 	DRIVER( sbm )		/* C69 (c) 1990 Taito Corporation (Japan) */
        //TODO 	DRIVER( selfeena )	/* ??? (c) 1991 East Technology */
        //TODO 	DRIVER( silentd )	/* ??? (c) 1992 Taito Corporation Japan (World) */
        //TODO 	DRIVER( silentdj )	/* ??? (c) 1992 Taito Corporation (Japan) */
        //TODO 	DRIVER( ryujin )	/* ??? (c) 1993 Taito Corporation (Japan) */
        //TODO 	DRIVER( qzshowby )	/* D72 (c) 1993 Taito Corporation (Japan) */
        //TODO 	DRIVER( pbobble )	/* ??? (c) 1994 Taito Corporation (Japan) */
        //TODO 	DRIVER( spacedx )	/* D89 (c) 1994 Taito Corporation (US) */
        //TODO 	DRIVER( spacedxj )	/* D89 (c) 1994 Taito Corporation (Japan) */
        //TODO 	DRIVER( spacedxo )	/* D89 (c) 1994 Taito Corporation (Japan) */
        //TODO 
        //TODO 	/* Taito Z-System games */
        //TODO 	DRIVER( contcirc )	/* B33 (c) 1989 Taito Corporation Japan (World) */
        //TODO 	DRIVER( contcrcu )	/* B33 (c) 1987 Taito America Corporation (US) */
        //TODO 	DRIVER( chasehq )	/* B52 (c) 1988 Taito Corporation Japan (World) */
        //TODO 	DRIVER( chasehqj )	/* B52 (c) 1988 Taito Corporation (Japan) */
        //TODO 	DRIVER( enforce )	/* B58 (c) 1988 Taito Corporation (Japan) */
        //TODO 	DRIVER( nightstr )	/* B91 (c) 1989 Taito America Corporation (US) */
        //TODO 	DRIVER( sci )		/* C09 (c) 1989 Taito Corporation Japan (World) */
        //TODO 	DRIVER( scia )		/* C09 (c) 1989 Taito Corporation Japan (World) */
        //TODO 	DRIVER( sciu )		/* C09 (c) 1989 Taito America Corporation (US) */
        //TODO 	DRIVER( bshark )	/* C34 (c) 1989 Taito America Corporation (US) */
        //TODO 	DRIVER( bsharkj )	/* C34 (c) 1989 Taito Corporation (Japan) */
        //TODO 	DRIVER( aquajack )	/* B77 (c) 1990 Taito Corporation Japan (World) */
        //TODO 	DRIVER( aquajckj )	/* B77 (c) 1990 Taito Corporation (Japan) */
        //TODO 	DRIVER( spacegun )	/* C57 (c) 1990 Taito Corporation Japan (World) */
        //TODO 	DRIVER( dblaxle )	/* C78 (c) 1991 Taito America Corporation (US) */
        //TODO 	DRIVER( pwheelsj )	/* C78 (c) 1991 Taito Corporation (Japan) */
        //TODO 
        //TODO 	/* Taito Air System games */
        //TODO TESTDRIVER( topland )	/* B62 (c) 1988 Taito Coporation Japan (World) */
        //TODO TESTDRIVER( ainferno )	/* C45 (c) 1990 Taito America Corporation (US) */
        //TODO 
        //TODO 	/* enhanced Z-System hardware games */
        //TODO 	DRIVER( gunbustr )	/* D27 (c) 1992 Taito Corporation (Japan) */
        //TODO 	DRIVER( superchs )	/* D46 (c) 1992 Taito America Corporation (US) */
        //TODO 	DRIVER( undrfire )	/* D67 (c) 1993 Taito Coporation Japan (World) */
        //TODO 
        //TODO 	/* Taito F2 games */
        //TODO 	DRIVER( finalb )	/* B82 (c) 1988 Taito Corporation Japan (World) */
        //TODO 	DRIVER( finalbj )	/* B82 (c) 1988 Taito Corporation (Japan) */
        //TODO 	DRIVER( dondokod )	/* B95 (c) 1989 Taito Corporation Japan (World) */
        //TODO 	DRIVER( dondokdu )	/* B95 (c) 1989 Taito America Corporation (US) */
        //TODO 	DRIVER( dondokdj )	/* B95 (c) 1989 Taito Corporation (Japan) */
        //TODO 	DRIVER( megab )		/* C11 (c) 1989 Taito Corporation Japan (World) */
        //TODO 	DRIVER( megabj )	/* C11 (c) 1989 Taito Corporation (Japan) */
        //TODO 	DRIVER( thundfox )	/* C28 (c) 1990 Taito Corporation Japan (World) */
        //TODO 	DRIVER( thndfoxu )	/* C28 (c) 1990 Taito America Corporation (US) */
        //TODO 	DRIVER( thndfoxj )	/* C28 (c) 1990 Taito Corporation (Japan) */
        //TODO 	DRIVER( cameltry )	/* C38 (c) 1989 Taito America Corporation (US) */
        //TODO 	DRIVER( camltrua )	/* C38 (c) 1989 Taito America Corporation (US) */
        //TODO 	DRIVER( cameltrj )	/* C38 (c) 1989 Taito Corporation (Japan) */
        //TODO 	DRIVER( qtorimon )	/* C41 (c) 1990 Taito Corporation (Japan) */
        //TODO 	DRIVER( liquidk )	/* C49 (c) 1990 Taito Corporation Japan (World) */
        //TODO 	DRIVER( liquidku )	/* C49 (c) 1990 Taito America Corporation (US) */
        //TODO 	DRIVER( mizubaku )	/* C49 (c) 1990 Taito Corporation (Japan) */
        //TODO 	DRIVER( quizhq )	/* C53 (c) 1990 Taito Corporation (Japan) */
        //TODO 	DRIVER( ssi )		/* C64 (c) 1990 Taito Corporation Japan (World) */
        //TODO 	DRIVER( majest12 )	/* C64 (c) 1990 Taito Corporation (Japan) */
        //TODO 	DRIVER( gunfront )	/* C71 (c) 1990 Taito Corporation Japan (World) */
        //TODO 	DRIVER( gunfronj )	/* C71 (c) 1990 Taito Corporation (Japan) */
        //TODO 	DRIVER( growl )		/* C74 (c) 1990 Taito Corporation Japan (World) */
        //TODO 	DRIVER( growlu )	/* C74 (c) 1990 Taito America Corporation (US) */
        //TODO 	DRIVER( runark )	/* C74 (c) 1990 Taito Corporation (Japan) */
        //TODO 	DRIVER( mjnquest )	/* C77 (c) 1990 Taito Corporation (Japan) */
        //TODO 	DRIVER( mjnquesb )	/* C77 (c) 1990 Taito Corporation (Japan) */
        //TODO 	DRIVER( footchmp )	/* C80 (c) 1990 Taito Corporation Japan (World) */
        //TODO 	DRIVER( hthero )	/* C80 (c) 1990 Taito Corporation (Japan) */
        //TODO 	DRIVER( euroch92 )	/*     (c) 1992 Taito Corporation Japan (World) */
        //TODO 	DRIVER( koshien )	/* C81 (c) 1990 Taito Corporation (Japan) */
        //TODO 	DRIVER( yuyugogo )	/* C83 (c) 1990 Taito Corporation (Japan) */
        //TODO 	DRIVER( ninjak )	/* C85 (c) 1990 Taito Corporation Japan (World) */
        //TODO 	DRIVER( ninjakj )	/* C85 (c) 1990 Taito Corporation (Japan) */
        //TODO 	DRIVER( solfigtr )	/* C91 (c) 1991 Taito Corporation Japan (World) */
        //TODO 	DRIVER( qzquest )	/* C92 (c) 1991 Taito Corporation (Japan) */
        //TODO 	DRIVER( pulirula )	/* C98 (c) 1991 Taito Corporation Japan (World) */
        //TODO 	DRIVER( pulirulj )	/* C98 (c) 1991 Taito Corporation (Japan) */
        //TODO 	DRIVER( metalb )	/* D16? (c) 1991 Taito Corporation Japan (World) */
        //TODO 	DRIVER( metalbj )	/* D12 (c) 1991 Taito Corporation (Japan) */
        //TODO 	DRIVER( qzchikyu )	/* D19 (c) 1991 Taito Corporation (Japan) */
        //TODO 	DRIVER( yesnoj )	/* D20 (c) 1992 Taito Corporation (Japan) */
        //TODO 	DRIVER( deadconx )	/* D28 (c) 1992 Taito Corporation Japan (World) */
        //TODO 	DRIVER( deadconj )	/* D28 (c) 1992 Taito Corporation (Japan) */
        //TODO 	DRIVER( dinorex )	/* D39 (c) 1992 Taito Corporation Japan (World) */
        //TODO 	DRIVER( dinorexj )	/* D39 (c) 1992 Taito Corporation (Japan) */
        //TODO 	DRIVER( dinorexu )	/* D39 (c) 1992 Taito America Corporation (US) */
        //TODO 	DRIVER( qjinsei )	/* D48 (c) 1992 Taito Corporation (Japan) */
        //TODO 	DRIVER( qcrayon )	/* D55 (c) 1993 Taito Corporation (Japan) */
        //TODO 	DRIVER( qcrayon2 )	/* D63 (c) 1993 Taito Corporation (Japan) */
        //TODO 	DRIVER( driftout )	/* (c) 1991 Visco */
        //TODO 	DRIVER( driveout )	/* bootleg */
        //TODO 
        //TODO 	/* Taito F3 games */
        //TODO 	DRIVER( ringrage )	/* D21 (c) 1992 Taito Corporation Japan (World) */
        //TODO 	DRIVER( ringragj )	/* D21 (c) 1992 Taito Corporation (Japan) */
        //TODO 	DRIVER( ringragu )	/* D21 (c) 1992 Taito America Corporation (US) */
        //TODO 	DRIVER( arabianm )	/* D29 (c) 1992 Taito Corporation Japan (World) */
        //TODO 	DRIVER( arabiamj )	/* D29 (c) 1992 Taito Corporation (Japan) */
        //TODO 	DRIVER( arabiamu )	/* D29 (c) 1992 Taito America Corporation (US) */
        //TODO 	DRIVER( ridingf )	/* D34 (c) 1992 Taito Corporation Japan (World) */
        //TODO 	DRIVER( ridefgtj )	/* D34 (c) 1992 Taito Corporation (Japan) */
        //TODO 	DRIVER( ridefgtu )	/* D34 (c) 1992 Taito America Corporation (US) */
        //TODO 	DRIVER( gseeker )	/* D40 (c) 1992 Taito Corporation Japan (World) */
        //TODO 	DRIVER( gseekerj )	/* D40 (c) 1992 Taito Corporation (Japan) */
        //TODO 	DRIVER( gseekeru )	/* D40 (c) 1992 Taito America Corporation (US) */
        //TODO 	DRIVER( hthero93 )	/* D49 (c) 1992 Taito Corporation (Japan) */
        //TODO 	DRIVER( cupfinal )	/* D49 (c) 1993 Taito Corporation Japan (World) */
        //TODO 	DRIVER( trstar )	/* D53 (c) 1993 Taito Corporation Japan (World) */
        //TODO 	DRIVER( trstarj )	/* D53 (c) 1993 Taito Corporation (Japan) */
        //TODO 	DRIVER( prmtmfgt )	/* D53 (c) 1993 Taito Corporation (US) */
        //TODO 	DRIVER( trstaro )	/* D53 (c) 1993 Taito Corporation (World) */
        //TODO 	DRIVER( trstaroj )	/* D53 (c) 1993 Taito Corporation (Japan) */
        //TODO 	DRIVER( prmtmfgo )	/* D53 (c) 1993 Taito Corporation (US) */
        //TODO 	DRIVER( gunlock )	/* D66 (c) 1993 Taito Corporation Japan (World) */
        //TODO 	DRIVER( rayforcj )	/* D66 (c) 1993 Taito Corporation (Japan) */
        //TODO 	DRIVER( rayforce )	/* D66 (c) 1993 Taito America Corporation (US) */
        //TODO 	DRIVER( scfinals )	/* D68 (c) 1993 Taito Corporation Japan (World) */
        //TODO 	DRIVER( dungeonm )	/* D69 (c) 1993 Taito Corporation Japan (World) */
        //TODO 	DRIVER( lightbr )	/* D69 (c) 1993 Taito Corporation (Japan) */
        //TODO 	DRIVER( dungenmu )	/* D69 (c) 1993 Taito America Corporation (US) */
        //TODO 	DRIVER( kaiserkn )	/* D84 (c) 1994 Taito Corporation Japan (World) */
        //TODO 	DRIVER( kaiserkj )	/* D84 (c) 1994 Taito Corporation (Japan) */
        //TODO 	DRIVER( gblchmp )	/* D84 (c) 1994 Taito America Corporation (US) */
        //TODO 	DRIVER( dankuga )	/* D84? (c) 1994 Taito Corporation (Japan) */
        //TODO 	DRIVER( dariusg )	/* D87 (c) 1994 Taito Corporation Japan (World) */
        //TODO 	DRIVER( dariusgj )	/* D87 (c) 1994 Taito Corporation (Japan) */
        //TODO 	DRIVER( dariusgu )	/* D87 (c) 1994 Taito America Corporation (US) */
        //TODO 	DRIVER( dariusgx )	/* D87 (c) 1994 Taito Corporation */
        //TODO 	DRIVER( bublbob2 )	/* D90 (c) 1994 Taito Corporation Japan (World) */
        //TODO 	DRIVER( bubsympe )	/* D90 (c) 1994 Taito Corporation Japan (Europe) */
        //TODO 	DRIVER( bubsympu )	/* D90 (c) 1994 Taito America Corporation (US) */
        //TODO 	DRIVER( bubsymph )	/* D90 (c) 1994 Taito Corporation (Japan) */
        //TODO 	DRIVER( spcinvdj )	/* D93 (c) 1994 Taito Corporation (Japan) */
        //TODO 	DRIVER( pwrgoal )	/* D94 (c) 1995 Taito Corporation Japan (World) */
        //TODO 	DRIVER( hthero95 )	/* D94 (c) 1995 Taito Corporation (Japan) */
        //TODO 	DRIVER( hthro95u )	/* D94 (c) 1995 Taito America Corporation (US) */
        //TODO 	DRIVER( qtheater )	/* D95 (c) 1994 Taito Corporation (Japan) */
        //TODO 	DRIVER( elvactr )	/* E02 (c) 1994 Taito Corporation Japan (World) */
        //TODO 	DRIVER( elvactrj )	/* E02 (c) 1994 Taito Corporation (Japan) */
        //TODO 	DRIVER( elvact2u )	/* E02 (c) 1994 Taito America Corporation (US) */
        //TODO 	DRIVER( spcinv95 )	/* E06 (c) 1995 Taito Corporation Japan (World) */
        //TODO 	DRIVER( spcnv95u )	/* E06 (c) 1995 Taito America Corporation (US) */
        //TODO 	DRIVER( akkanvdr )	/* E06 (c) 1995 Taito Corporation (Japan) */
        //TODO 	DRIVER( twinqix )	/* ??? (c) 1995 Taito America Corporation (US) */
        //TODO 	DRIVER( quizhuhu )	/* E08 (c) 1995 Taito Corporation (Japan) */
        //TODO 	DRIVER( pbobble2 )	/* E10 (c) 1995 Taito Corporation Japan (World) */
        //TODO 	DRIVER( pbobbl2j )	/* E10 (c) 1995 Taito Corporation (Japan) */
        //TODO 	DRIVER( pbobbl2u )	/* E10 (c) 1995 Taito America Corporation (US) */
        //TODO 	DRIVER( pbobbl2x )	/* E10 (c) 1995 Taito Corporation (Japan) */
        //TODO 	DRIVER( gekirido )	/* E11 (c) 1995 Taito Corporation (Japan) */
        //TODO 	DRIVER( ktiger2 )	/* E15 (c) 1995 Taito Corporation (Japan) */
        //TODO 	DRIVER( bubblem )	/* E21 (c) 1995 Taito Corporation Japan (World) */
        //TODO 	DRIVER( bubblemj )	/* E21 (c) 1995 Taito Corporation (Japan) */
        //TODO 	DRIVER( cleopatr )	/* E28 (c) 1996 Taito Corporation (Japan) */
        //TODO 	DRIVER( pbobble3 )	/* E29 (c) 1996 Taito Corporation (World) */
        //TODO 	DRIVER( pbobbl3u )	/* E29 (c) 1996 Taito Corporation (US) */
        //TODO 	DRIVER( pbobbl3j )	/* E29 (c) 1996 Taito Corporation (Japan) */
        //TODO 	DRIVER( arkretrn )	/* E36 (c) 1997 Taito Corporation (Japan) */
        //TODO 	DRIVER( kirameki )	/* E44 (c) 1997 Taito Corporation (Japan) */
        //TODO 	DRIVER( puchicar )	/* E46 (c) 1997 Taito Corporation (Japan) */
        //TODO 	DRIVER( pbobble4 )	/* E49 (c) 1997 Taito Corporation (World) */
        //TODO 	DRIVER( pbobbl4j )	/* E49 (c) 1997 Taito Corporation (Japan) */
        //TODO 	DRIVER( pbobbl4u )	/* E49 (c) 1997 Taito Corporation (US) */
        //TODO 	DRIVER( popnpop )	/* E51 (c) 1997 Taito Corporation (World) */
        //TODO 	DRIVER( popnpopj )	/* E51 (c) 1997 Taito Corporation (Japan) */
        //TODO 	DRIVER( popnpopu )	/* E51 (c) 1997 Taito Corporation (US) */
        //TODO 	DRIVER( landmakr )	/* E61 (c) 1998 Taito Corporation (Japan) */
        //TODO 
        //TODO 	/* Toaplan games */
        //TODO 	DRIVER( perfrman )	/* (c) 1985 Data East Corporation (Japan) */
        //TODO 	DRIVER( perfrmau )	/* (c) 1985 Data East USA (US) */
        //TODO 	DRIVER( tigerh )	/* GX-551 [not a Konami board!] */
        //TODO 	DRIVER( tigerh2 )	/* GX-551 [not a Konami board!] */
        //TODO 	DRIVER( tigerhj )	/* GX-551 [not a Konami board!] */
        //TODO 	DRIVER( tigerhb1 )	/* bootleg but (c) 1985 Taito Corporation */
        //TODO 	DRIVER( tigerhb2 )	/* bootleg but (c) 1985 Taito Corporation */
        //TODO 	DRIVER( slapfigh )	/* TP-??? */
        //TODO 	DRIVER( slapbtjp )	/* bootleg but (c) 1986 Taito Corporation */
        //TODO 	DRIVER( slapbtuk )	/* bootleg but (c) 1986 Taito Corporation */
        //TODO 	DRIVER( alcon )		/* TP-??? */
        //TODO 	DRIVER( getstar )
        //TODO 	DRIVER( getstarj )
        //TODO 	DRIVER( getstarb )	/* GX-006 bootleg but (c) 1986 Taito Corporation */
        //TODO 	DRIVER( mjsister )	/* (c) 1986 Toaplan */
        //TODO 
        //TODO 	DRIVER( fshark )	/* TP-007 (c) 1987 Taito Corporation (World) */
        //TODO 	DRIVER( skyshark )	/* TP-007 (c) 1987 Taito America Corporation + Romstar license (US) */
        //TODO 	DRIVER( hishouza )	/* TP-007 (c) 1987 Taito Corporation (Japan) */
        //TODO 	DRIVER( fsharkbt )	/* bootleg */
        //TODO 	DRIVER( wardner )	/* TP-009 (c) 1987 Taito Corporation Japan (World) */
        //TODO 	DRIVER( pyros )		/* TP-009 (c) 1987 Taito America Corporation (US) */
        //TODO 	DRIVER( wardnerj )	/* TP-009 (c) 1987 Taito Corporation (Japan) */
        //TODO 	DRIVER( twincobr )	/* TP-011 (c) 1987 Taito Corporation (World) */
        //TODO 	DRIVER( twincobu )	/* TP-011 (c) 1987 Taito America Corporation + Romstar license (US) */
        //TODO 	DRIVER( ktiger )	/* TP-011 (c) 1987 Taito Corporation (Japan) */
        //TODO 	DRIVER( gulfwar2 )	/* (c) 1991 Comad */
        //TODO 
        //TODO 	DRIVER( rallybik )	/* TP-012 (c) 1988 Taito */
        //TODO 	DRIVER( truxton )	/* TP-013B (c) 1988 Taito */
        //TODO 	DRIVER( hellfire )	/* TP-??? (c) 1989 Toaplan + Taito license */
        //TODO 	DRIVER( hellfir1 )	/* TP-??? (c) 1989 Toaplan + Taito license */
        //TODO 	DRIVER( zerowing )	/* TP-015 (c) 1989 Toaplan */
        //TODO 	DRIVER( demonwld )	/* TP-016 (c) 1990 Toaplan (+ Taito license when set to Japan) */
        //TODO 	DRIVER( demonwl1 )	/* TP-016 (c) 1989 Toaplan + Taito license */
        //TODO 	DRIVER( fireshrk )	/* TP-017 (c) 1990 Toaplan */
        //TODO 	DRIVER( samesame )	/* TP-017 (c) 1989 Toaplan */
        //TODO 	DRIVER( samesam2 )	/* TP-017 (c) 1989 Toaplan */
        //TODO 	DRIVER( outzone )	/* TP-018 (c) 1990 Toaplan */
        //TODO 	DRIVER( outzonea )	/* TP-018 (c) 1990 Toaplan */
        //TODO 	DRIVER( vimana )	/* TP-019 (c) 1991 Toaplan (+ Tecmo license when set to Japan) */
        //TODO 	DRIVER( vimana1 )	/* TP-019 (c) 1991 Toaplan (+ Tecmo license when set to Japan)  */
        //TODO 	DRIVER( vimanan )	/* TP-019 (c) 1991 Toaplan (+ Nova Apparate GMBH & Co license) */
        //TODO 	DRIVER( snowbros )	/* MIN16-02 (c) 1990 Toaplan + Romstar license */
        //TODO 	DRIVER( snowbroa )	/* MIN16-02 (c) 1990 Toaplan + Romstar license */
        //TODO 	DRIVER( snowbrob )	/* MIN16-02 (c) 1990 Toaplan + Romstar license */
        //TODO 	DRIVER( snowbroj )	/* MIN16-02 (c) 1990 Toaplan */
        //TODO 	DRIVER( wintbob )	/* bootleg */
        //TODO 	DRIVER( snowbro3 )	/* (c) 2002 Syrmex (hack) */
        //TODO 
        //TODO 	DRIVER( tekipaki )	/* TP-020 (c) 1991 Toaplan */
        //TODO 	DRIVER( ghox )		/* TP-021 (c) 1991 Toaplan */
        //TODO 	DRIVER( dogyuun )	/* TP-022 (c) 1992 Toaplan */
        //TODO 	DRIVER( kbash )		/* TP-023 (c) 1993 Toaplan */
        //TODO 	DRIVER( truxton2 )	/* TP-024 (c) 1992 Toaplan */
        //TODO 	DRIVER( pipibibs )	/* TP-025 */
        //TODO 	DRIVER( whoopee )	/* TP-025 */
        //TODO 	DRIVER( pipibibi )	/* (c) 1991 Ryouta Kikaku (bootleg?) */
        //TODO TESTDRIVER( fixeight )	/* TP-026 (c) 1992 + Taito license */
        //TODO 	DRIVER( vfive )		/* TP-027 (c) 1993 Toaplan (Japan) */
        //TODO 	DRIVER( grindstm )	/* TP-027 (c) 1993 Toaplan + Unite Trading license (Korea) */
        //TODO 	DRIVER( grindsta )	/* TP-027 (c) 1993 Toaplan + Unite Trading license (Korea) */
        //TODO 	DRIVER( batsugun )	/* TP-030 (c) 1993 Toaplan */
        //TODO 	DRIVER( batugnsp )	/* TP-??? (c) 1993 Toaplan */
        //TODO 	DRIVER( snowbro2 )	/* TP-??? (c) 1994 Hanafram */
        //TODO 	/* see http://www.vsa-ag.ch/r8zing/ for a list of Raizing/8ing games */
        //TODO 	DRIVER( mahoudai )	/* (c) 1993 Raizing + Able license */
        //TODO 	DRIVER( shippumd )	/* (c) 1994 Raizing/8ing */
        //TODO 	DRIVER( battleg )	/* (c) 1996 Raizing/8ing */
        //TODO 	DRIVER( battlega )	/* (c) 1996 Raizing/8ing */
        //TODO 	DRIVER( batrider )	/* (c) 1998 Raizing/8ing */
        //TODO 	DRIVER( batridra )	/* (c) 1998 Raizing/8ing */
        //TODO 	DRIVER( bbakraid )	/* (c) 1999 8ing */
        //TODO 	DRIVER( bbakrada )	/* (c) 1999 8ing */
        //TODO 
        //TODO /*
        //TODO Toa Plan's board list
        //TODO (translated from http://www.aianet.ne.jp/~eisetu/rom/rom_toha.html)
        //TODO 
        //TODO Title              ROMno.   Remark(1)   Remark(2)
        //TODO --------------------------------------------------
        //TODO Tiger Heli           A47      GX-551
        //TODO Hishouzame           B02      TP-007
        //TODO Kyukyoku Tiger       B30      TP-011
        //TODO Dash Yarou           B45      TP-012
        //TODO Tatsujin             B65      TP-013B   M6100649A
        //TODO Zero Wing            O15      TP-015
        //TODO Horror Story         O16      TP-016
        //TODO Same!Same!Same!      O17      TP-017
        //TODO Out Zone                      TP-018
        //TODO Vimana                        TP-019
        //TODO Teki Paki            O20      TP-020
        //TODO Ghox               TP-21      TP-021
        //TODO Dogyuun                       TP-022
        //TODO Tatsujin Oh                   TP-024    *1
        //TODO Fixeight                      TP-026
        //TODO V-V                           TP-027
        //TODO 
        //TODO *1 There is a doubt this game uses TP-024 board and TP-025 romsets.
        //TODO 
        //TODO    86 Mahjong Sisters                                 Kit 2P 8W+2B     HC    Mahjong TP-
        //TODO    88 Dash                                            Kit 2P 8W+2B                   TP-
        //TODO    89 Fire Shark                                      Kit 2P 8W+2B     VC    Shooter TP-017
        //TODO    89 Twin Hawk                                       Kit 2P 8W+2B     VC    Shooter TP-
        //TODO    91 Whoopie                                         Kit 2P 8W+2B     HC    Action
        //TODO    92 Teki Paki                                       Kit 2P                         TP-020
        //TODO    92 Ghox                                            Kit 2P Paddle+1B VC    Action  TP-021
        //TODO 10/92 Dogyuun                                         Kit 2P 8W+2B     VC    Shooter TP-022
        //TODO 92/93 Knuckle Bash                 Atari Games        Kit 2P 8W+2B     HC    Action  TP-023
        //TODO 10/92 Tatsujin II/Truxton II       Taito              Kit 2P 8W+2B     VC    Shooter TP-024
        //TODO 10/92 Truxton II/Tatsujin II       Taito              Kit 2P 8W+2B     VC    Shooter TP-024
        //TODO       Pipi & Bipi                                                                    TP-025
        //TODO    92 Fix Eight                                       Kit 2P 8W+2B     VC    Action  TP-026
        //TODO 12/92 V  -  V (5)/Grind Stormer                       Kit 2P 8W+2B     VC    Shooter TP-027
        //TODO  1/93 Grind Stormer/V - V (Five)                      Kit 2P 8W+2B     VC    Shooter TP-027
        //TODO  2/94 Batsugun                                        Kit 2P 8W+2B     VC            TP-
        //TODO  4/94 Snow Bros. 2                                    Kit 2P 8W+2B     HC    Action  TP-
        //TODO */
        //TODO 
        //TODO 	/* Cave games */
        //TODO 	/* Cave was formed in 1994 from the ruins of Toaplan, like Raizing was. */
        //TODO TESTDRIVER( pwrinst2 )	/* (c) 1994 Atlus */
        //TODO 	DRIVER( mazinger )	/* (c) 1994 Banpresto (country is in EEPROM) */
        //TODO 	DRIVER( donpachi )	/* (c) 1995 Atlus/Cave */
        //TODO 	DRIVER( donpachk )	/* (c) 1995 Atlus/Cave */
        //TODO 	DRIVER( metmqstr )	/* (c) 1995 Banpresto / Pandorabox */
        //TODO 	DRIVER( sailormn )	/* (c) 1995 Banpresto (country is in EEPROM) */
        //TODO 	DRIVER( sailormo )	/* (c) 1995 Banpresto (country is in EEPROM) */
        //TODO 	DRIVER( agallet )	/* (c) 1996 Banpresto / Gazelle (country is in EEPROM) */
        //TODO 	DRIVER( hotdogst )	/* (c) 1996 Marble */
        //TODO 	DRIVER( ddonpach )	/* (c) 1997 Atlus/Cave */
        //TODO 	DRIVER( dfeveron )	/* (c) 1998 Cave + Nihon System license */
        //TODO 	DRIVER( esprade )	/* (c) 1998 Atlus/Cave */
        //TODO 	DRIVER( espradej )	/* (c) 1998 Atlus/Cave (Japan) */
        //TODO 	DRIVER( espradeo )	/* (c) 1998 Atlus/Cave (Japan) */
        //TODO 	DRIVER( uopoko )	/* (c) 1998 Cave + Jaleco license */
        //TODO 	DRIVER( guwange )	/* (c) 1999 Atlus/Cave */
        //TODO 
        //TODO 	/* SemiCom games */
        //TODO 	DRIVER( hyperpac )	/* (c) 1995 SemiCom */
        //TODO 	DRIVER( hyperpcb )	/* bootleg */
        //TODO TESTDRIVER( moremorp )
        //TODO TESTDRIVER( 3in1semi )
        //TODO TESTDRIVER( cookbib2 )
        //TODO TESTDRIVER( htchctch )
        //TODO 
        //TODO 	/* Kyugo games */
        //TODO 	/* Kyugo only made four games: Repulse, Flash Gal, SRD Mission and Air Wolf. */
        //TODO 	/* Gyrodine was made by Crux. Crux was antecedent of Toa Plan, and spin-off from Orca. */
        //TODO 	DRIVER( gyrodine )	/* (c) 1984 Taito Corporation */
        //TODO 	DRIVER( sonofphx )	/* (c) 1985 Associated Overseas MFR */
        //TODO 	DRIVER( repulse )	/* (c) 1985 Sega */
        //TODO 	DRIVER( 99lstwar )	/* (c) 1985 Proma */
        //TODO 	DRIVER( 99lstwra )	/* (c) 1985 Proma */
        //TODO 	DRIVER( flashgal )	/* (c) 1985 Sega */
        //TODO 	DRIVER( srdmissn )	/* (c) 1986 Taito Corporation */
        //TODO 	DRIVER( legend )	/* no copyright notice [1986 Sega/Coreland?] */
        //TODO 	DRIVER( airwolf )	/* (c) 1987 Kyugo */
        //TODO 	DRIVER( skywolf )	/* bootleg */
        //TODO 	DRIVER( skywolf2 )	/* bootleg */
        //TODO 
        //TODO 	/* Williams games */
        //TODO 	DRIVER( defender )	/* (c) 1980 */
        //TODO 	DRIVER( defendg )	/* (c) 1980 */
        //TODO 	DRIVER( defendw )	/* (c) 1980 */
        //TODO TESTDRIVER( defndjeu )	/* bootleg */
        //TODO 	DRIVER( defcmnd )	/* bootleg */
        //TODO 	DRIVER( defence )	/* bootleg */
        //TODO 	DRIVER( mayday )
        //TODO 	DRIVER( maydaya )
        //TODO 	DRIVER( maydayb )
        //TODO 	DRIVER( colony7 )	/* (c) 1981 Taito */
        //TODO 	DRIVER( colony7a )	/* (c) 1981 Taito */
        //TODO 	DRIVER( stargate )	/* (c) 1981 */
        //TODO 	DRIVER( robotron )	/* (c) 1982 */
        //TODO 	DRIVER( robotryo )	/* (c) 1982 */
        //TODO 	DRIVER( joust )		/* (c) 1982 */
        //TODO 	DRIVER( joustr )	/* (c) 1982 */
        //TODO 	DRIVER( joustwr )	/* (c) 1982 */
        //TODO 	DRIVER( bubbles )	/* (c) 1982 */
        //TODO 	DRIVER( bubblesr )	/* (c) 1982 */
        //TODO 	DRIVER( bubblesp )	/* (c) 1982 */
        //TODO 	DRIVER( splat )		/* (c) 1982 */
        //TODO 	DRIVER( sinistar )	/* (c) 1982 */
        //TODO 	DRIVER( sinista1 )	/* (c) 1982 */
        //TODO 	DRIVER( sinista2 )	/* (c) 1982 */
        //TODO 	DRIVER( playball )	/* (c) 1983 */
        //TODO 	DRIVER( blaster )	/* (c) 1983 */
        //TODO 	DRIVER( blastkit )	/* (c) 1983 */
        //TODO 	DRIVER( spdball )	/* (c) 1985 */
        //TODO 	DRIVER( mysticm )	/* (c) 1983 */
        //TODO 	DRIVER( tshoot )	/* (c) 1984 */
        //TODO 	DRIVER( inferno )	/* (c) 1984 */
        //TODO 	DRIVER( joust2 )	/* (c) 1986 */
        //TODO 	DRIVER( lottofun )	/* (c) 1987 H.A.R. Management */
        //TODO 
        //TODO 	/* Capcom games */
        //TODO 	/* The following is a COMPLETE list of the Capcom games up to 1997, as shown on */
        //TODO 	/* their web site. The list is sorted by production date.                       */
        //TODO 	/* A comprehensive list of Capcom games with board info can be found here:      */
        //TODO 	/* http://www.arcadeflyers.com/strider/capcom_list.html                         */
        //TODO 	DRIVER( vulgus )	/*  5/1984 (c) 1984 */
        //TODO 	DRIVER( vulgus2 )	/*  5/1984 (c) 1984 */
        //TODO 	DRIVER( vulgusj )	/*  5/1984 (c) 1984 */
        //TODO 	DRIVER( sonson )	/*  7/1984 (c) 1984 */
        //TODO 	DRIVER( sonsonj )	/*  7/1984 (c) 1984 (Japan) */
        //TODO 	DRIVER( higemaru )	/*  9/1984 (c) 1984 */
        //TODO 	DRIVER( 1942 )		/* 12/1984 (c) 1984 */
        //TODO 	DRIVER( 1942a )		/* 12/1984 (c) 1984 */
        //TODO 	DRIVER( 1942b )		/* 12/1984 (c) 1984 */
        //TODO 	DRIVER( exedexes )	/*  2/1985 (c) 1985 */
        //TODO 	DRIVER( savgbees )	/*  2/1985 (c) 1985 + Memetron license */
        //TODO 	DRIVER( commando )	/*  5/1985 (c) 1985 (World) */
        //TODO 	DRIVER( commandu )	/*  5/1985 (c) 1985 + Data East license (US) */
        //TODO 	DRIVER( commandj )	/*  5/1985 (c) 1985 (Japan) */
        //TODO 	DRIVER( spaceinv )	/* bootleg */
        //TODO 	DRIVER( gng )		/*  9/1985 (c) 1985 */
        //TODO 	DRIVER( gnga )		/*  9/1985 (c) 1985 */
        //TODO 	DRIVER( gngt )		/*  9/1985 (c) 1985 */
        //TODO 	DRIVER( makaimur )	/*  9/1985 (c) 1985 */
        //TODO 	DRIVER( makaimuc )	/*  9/1985 (c) 1985 */
        //TODO 	DRIVER( makaimug )	/*  9/1985 (c) 1985 */
        //TODO 	DRIVER( diamond )	/* (c) 1989 KH Video (NOT A CAPCOM GAME but runs on GnG hardware) */
        //TODO 	DRIVER( gunsmoke )	/* 11/1985 (c) 1985 (World) */
        //TODO 	DRIVER( gunsmoku )	/* 11/1985 (c) 1985 + Romstar (US) */
        //TODO 	DRIVER( gunsmoka )	/* 11/1985 (c) 1985 (US) */
        //TODO 	DRIVER( gunsmokj )	/* 11/1985 (c) 1985 (Japan) */
        //TODO 	DRIVER( sectionz )	/* 12/1985 (c) 1985 */
        //TODO 	DRIVER( sctionza )	/* 12/1985 (c) 1985 */
        //TODO 	DRIVER( trojan )	/*  4/1986 (c) 1986 (US) */
        //TODO 	DRIVER( trojanr )	/*  4/1986 (c) 1986 + Romstar */
        //TODO 	DRIVER( trojanj )	/*  4/1986 (c) 1986 (Japan) */
        //TODO 	DRIVER( srumbler )	/*  9/1986 (c) 1986 */
        //TODO 	DRIVER( srumblr2 )	/*  9/1986 (c) 1986 */
        //TODO 	DRIVER( rushcrsh )	/*  9/1986 (c) 1986 */
        //TODO 	DRIVER( lwings )	/* 11/1986 (c) 1986 */
        //TODO 	DRIVER( lwings2 )	/* 11/1986 (c) 1986 */
        //TODO 	DRIVER( lwingsjp )	/* 11/1986 (c) 1986 */
        //TODO 	DRIVER( sidearms )	/* 12/1986 (c) 1986 (World) */
        //TODO 	DRIVER( sidearmr )	/* 12/1986 (c) 1986 + Romstar license (US) */
        //TODO 	DRIVER( sidearjp )	/* 12/1986 (c) 1986 (Japan) */
        //TODO 	DRIVER( turtship )	/* (c) 1988 Philco (NOT A CAPCOM GAME but runs on modified Sidearms hardware) */
        //TODO 	DRIVER( dyger )		/* (c) 1989 Philco (NOT A CAPCOM GAME but runs on modified Sidearms hardware) */
        //TODO 	DRIVER( dygera )	/* (c) 1989 Philco (NOT A CAPCOM GAME but runs on modified Sidearms hardware) */
        //TODO 	DRIVER( avengers )	/*  2/1987 (c) 1987 (US) */
        //TODO 	DRIVER( avenger2 )	/*  2/1987 (c) 1987 (US) */
        //TODO 	DRIVER( buraiken )	/*  2/1987 (c) 1987 (Japan) */
        //TODO 	DRIVER( bionicc )	/*  3/1987 (c) 1987 (US) */
        //TODO 	DRIVER( bionicc2 )	/*  3/1987 (c) 1987 (US) */
        //TODO 	DRIVER( topsecrt )	/*  3/1987 (c) 1987 (Japan) */
        //TODO 	DRIVER( 1943 )		/*  6/1987 (c) 1987 (US) */
        //TODO 	DRIVER( 1943j )		/*  6/1987 (c) 1987 (Japan) */
        //TODO 	DRIVER( blktiger )	/*  8/1987 (c) 1987 (US) */
        //TODO 	DRIVER( bktigerb )	/* bootleg */
        //TODO 	DRIVER( blkdrgon )	/*  8/1987 (c) 1987 (Japan) */
        //TODO 	DRIVER( blkdrgnb )	/* bootleg, hacked to say Black Tiger */
        //TODO 	DRIVER( sf1 )		/*  8/1987 (c) 1987 (World) */
        //TODO 	DRIVER( sf1us )		/*  8/1987 (c) 1987 (US) */
        //TODO 	DRIVER( sf1jp )		/*  8/1987 (c) 1987 (Japan) */
        //TODO 	DRIVER( sf1p )		/*  8/1987 (c) 1987 */
        //TODO 	DRIVER( tigeroad )	/* 11/1987 (c) 1987 + Romstar (US) */
        //TODO 	DRIVER( toramich )	/* 11/1987 (c) 1987 (Japan) */
        //TODO 	DRIVER( f1dream )	/*  4/1988 (c) 1988 + Romstar */
        //TODO 	DRIVER( f1dreamb )	/* bootleg */
        //TODO 	DRIVER( 1943kai )	/*  6/1988 (c) 1987 (Japan) */
        //TODO 	DRIVER( lastduel )	/*  7/1988 (c) 1988 (US) */
        //TODO 	DRIVER( lstduela )	/*  7/1988 (c) 1988 (US) */
        //TODO 	DRIVER( lstduelb )	/* bootleg */
        //TODO 	DRIVER( madgear )	/*  2/1989 (c) 1989 (US) */
        //TODO 	DRIVER( madgearj )	/*  2/1989 (c) 1989 (Japan) */
        //TODO 	DRIVER( ledstorm )	/*  2/1989 (c) 1989 (US) */
        //TODO 	/*  3/1989 Dokaben (baseball) - see below among "Mitchell" games */
        //TODO 	/*  8/1989 Dokaben 2 (baseball) - see below among "Mitchell" games */
        //TODO 	/* 10/1989 Capcom Baseball - see below among "Mitchell" games */
        //TODO 	/* 11/1989 Capcom World - see below among "Mitchell" games */
        //TODO 	/*  3/1990 Adventure Quiz 2 Hatena no Dai-Bouken - see below among "Mitchell" games */
        //TODO 	/*  1/1991 Quiz Tonosama no Yabou - see below among "Mitchell" games */
        //TODO 	/*  4/1991 Ashita Tenki ni Naare (golf) - see below among "Mitchell" games */
        //TODO 	/*  5/1991 Ataxx - see below among "Leland" games */
        //TODO 	/*  6/1991 Quiz Sangokushi - see below among "Mitchell" games */
        //TODO 	/* 10/1991 Block Block - see below among "Mitchell" games */
        //TODO 	/*  6/1995 Street Fighter - the Movie - see below among "Incredible Technologies" games */
        //TODO 
        //TODO #endif /* CPSMAME */
        //TODO 
        //TODO 	/* Capcom CPS1 games */
        //TODO 	DRIVER( forgottn )	/*  7/1988 (c) 1988 (US) */
        //TODO 	DRIVER( lostwrld )	/*  7/1988 (c) 1988 (Japan) */
        //TODO 	DRIVER( ghouls )	/* 12/1988 (c) 1988 (World) */
        //TODO 	DRIVER( ghoulsu )	/* 12/1988 (c) 1988 (US) */
        //TODO 	DRIVER( daimakai )	/* 12/1988 (c) 1988 (Japan) */
        //TODO 	DRIVER( strider )	/*  3/1989 (c) 1989 (not explicitly stated but should be US) */
        //TODO 	DRIVER( striderj )	/*  3/1989 (c) 1989 */
        //TODO 	DRIVER( stridrja )	/*  3/1989 (c) 1989 */
        //TODO 	DRIVER( dw )		/*  4/1989 (c) 1989 (World) */
        //TODO 	DRIVER( dwj )		/*  4/1989 (c) 1989 (Japan) */
        //TODO 	DRIVER( willow )	/*  6/1989 (c) 1989 (US) */
        //TODO 	DRIVER( willowj )	/*  6/1989 (c) 1989 (Japan) */
        //TODO 	DRIVER( willowje )	/*  6/1989 (c) 1989 (Japan) */
        //TODO 	DRIVER( unsquad )	/*  8/1989 (c) 1989 */
        //TODO 	DRIVER( area88 )	/*  8/1989 (c) 1989 */
        //TODO 	DRIVER( ffight )	/* 12/1989 (c) (World) */
        //TODO 	DRIVER( ffightu )	/* 12/1989 (c) (US)    */
        //TODO 	DRIVER( ffightj )	/* 12/1989 (c) (Japan) */
        //TODO 	DRIVER( 1941 )		/*  2/1990 (c) 1990 (World) */
        //TODO 	DRIVER( 1941j )		/*  2/1990 (c) 1990 (Japan) */
        //TODO 	DRIVER( mercs )		/* 02/03/1990 (c) 1990 (World) */
        //TODO 	DRIVER( mercsu )	/* 02/03/1990 (c) 1990 (US)    */
        //TODO 	DRIVER( mercsj )	/* 02/03/1990 (c) 1990 (Japan) */
        //TODO 	DRIVER( mtwins )	/* 19/06/1990 (c) 1990 (World) */
        //TODO 	DRIVER( chikij )	/* 19/06/1990 (c) 1990 (Japan) */
        //TODO 	DRIVER( msword )	/* 25/07/1990 (c) 1990 (World) */
        //TODO 	DRIVER( mswordr1 )	/* 23/06/1990 (c) 1990 (World) */
        //TODO 	DRIVER( mswordu )	/* 25/07/1990 (c) 1990 (US)    */
        //TODO 	DRIVER( mswordj )	/* 23/06/1990 (c) 1990 (Japan) */
        //TODO 	DRIVER( cawing )	/* 12/10/1990 (c) 1990 (World) */
        //TODO 	DRIVER( cawingu )	/* 12/10/1990 (c) 1990 (US) */
        //TODO 	DRIVER( cawingj )	/* 12/10/1990 (c) 1990 (Japan) */
        //TODO 	DRIVER( nemo )		/* 30/11/1990 (c) 1990 (World) */
        //TODO 	DRIVER( nemoj )		/* 20/11/1990 (c) 1990 (Japan) */
        //TODO 	DRIVER( sf2 )		/* 22/05/1991 (c) 1991 (World) */
        //TODO 	DRIVER( sf2eb )		/* 14/02/1991 (c) 1991 (World) */
        //TODO 	DRIVER( sf2ua )		/* 06/02/1991 (c) 1991 (US)    */
        //TODO 	DRIVER( sf2ub )		/* 14/02/1991 (c) 1991 (US)    */
        //TODO 	DRIVER( sf2ud )		/* 18/03/1991 (c) 1991 (US)    */
        //TODO 	DRIVER( sf2ue )		/* 28/02/1991 (c) 1991 (US)    */
        //TODO 	DRIVER( sf2uf )		/* 11/04/1991 (c) 1991 (US)    */
        //TODO 	DRIVER( sf2ui )		/* 22/05/1991 (c) 1991 (US)    */
        //TODO 	DRIVER( sf2j )		/* 10/12/1991 (c) 1991 (Japan) */
        //TODO 	DRIVER( sf2ja )		/* 14/02/1991 (c) 1991 (Japan) */
        //TODO 	DRIVER( sf2jc )		/* 06/03/1991 (c) 1991 (Japan) */
        //TODO 	DRIVER( 3wonders )	/* 20/05/1991 (c) 1991 (World) */
        //TODO 	DRIVER( 3wonderu )	/* 20/05/1991 (c) 1991 (US)    */
        //TODO 	DRIVER( wonder3 )	/* 20/05/1991 (c) 1991 (Japan) */
        //TODO 	DRIVER( kod )		/* 11/07/1991 (c) 1991 (World) */
        //TODO 	DRIVER( kodu )		/* 10/09/1991 (c) 1991 (US)    */
        //TODO 	DRIVER( kodj )		/* 05/08/1991 (c) 1991 (Japan) */
        //TODO 	DRIVER( kodb )		/* bootleg */
        //TODO 	DRIVER( captcomm )	/* 14/10/1991 (c) 1991 (World) */
        //TODO 	DRIVER( captcomu )	/* 28/ 9/1991 (c) 1991 (US)    */
        //TODO 	DRIVER( captcomj )	/* 02/12/1991 (c) 1991 (Japan) */
        //TODO 	DRIVER( knights )	/* 27/11/1991 (c) 1991 (World) */
        //TODO 	DRIVER( knightsu )	/* 27/11/1991 (c) 1991 (US)    */
        //TODO 	DRIVER( knightsj )	/* 27/11/1991 (c) 1991 (Japan) */
        //TODO 	DRIVER( sf2ce )		/* 13/03/1992 (c) 1992 (World) */
        //TODO 	DRIVER( sf2ceua )	/* 13/03/1992 (c) 1992 (US)    */
        //TODO 	DRIVER( sf2ceub )	/* 13/05/1992 (c) 1992 (US)    */
        //TODO 	DRIVER( sf2ceuc )	/* 03/08/1992 (c) 1992 (US)    */
        //TODO 	DRIVER( sf2cej )	/* 13/05/1992 (c) 1992 (Japan) */
        //TODO 	DRIVER( sf2rb )		/* hack */
        //TODO 	DRIVER( sf2rb2 )	/* hack */
        //TODO 	DRIVER( sf2red )	/* hack */
        //TODO 	DRIVER( sf2v004 )	/* hack */
        //TODO 	DRIVER( sf2accp2 )	/* hack */
        //TODO 	DRIVER( varth )		/* 12/06/1992 (c) 1992 (World) */
        //TODO 	DRIVER( varthu )	/* 12/06/1992 (c) 1992 (US) */
        //TODO 	DRIVER( varthj )	/* 14/07/1992 (c) 1992 (Japan) */
        //TODO 	DRIVER( cworld2j )	/* 11/06/1992 (QUIZ 5) (c) 1992 (Japan) */
        //TODO 	DRIVER( wof )		/* 02/10/1992 (c) 1992 (World) (CPS1 + QSound) */
        //TODO 	DRIVER( wofa )		/* 05/10/1992 (c) 1992 (Asia)  (CPS1 + QSound) */
        //TODO 	DRIVER( wofu )		/* 31/10/1992 (c) 1992 (US) (CPS1 + QSound) */
        //TODO 	DRIVER( wofj )		/* 31/10/1992 (c) 1992 (Japan) (CPS1 + QSound) */
        //TODO 	DRIVER( sf2t )		/* 09/12/1992 (c) 1992 (US)    */
        //TODO 	DRIVER( sf2tj )		/* 09/12/1992 (c) 1992 (Japan) */
        //TODO 	DRIVER( dino )		/* 01/02/1993 (c) 1993 (World) (CPS1 + QSound) */
        //TODO 	DRIVER( dinou )		/* 01/02/1993 (c) 1993 (US)    (CPS1 + QSound) */
        //TODO 	DRIVER( dinoj )		/* 01/02/1993 (c) 1993 (Japan) (CPS1 + QSound) */
        //TODO 	DRIVER( punisher )	/* 22/04/1993 (c) 1993 (World) (CPS1 + QSound) */
        //TODO 	DRIVER( punishru )	/* 22/04/1993 (c) 1993 (US)    (CPS1 + QSound) */
        //TODO 	DRIVER( punishrj )	/* 22/04/1993 (c) 1993 (Japan) (CPS1 + QSound) */
        //TODO 	DRIVER( slammast )	/* 13/07/1993 (c) 1993 (World) (CPS1 + QSound) */
        //TODO 	DRIVER( slammasu )	/* 13/07/1993 (c) 1993 (US)    (CPS1 + QSound) */
        //TODO 	DRIVER( mbomberj )	/* 13/07/1993 (c) 1993 (Japan) (CPS1 + QSound) */
        //TODO 	DRIVER( mbombrd )	/* 06/12/1993 (c) 1993 (World) (CPS1 + QSound) */
        //TODO 	DRIVER( mbombrdj )	/* 06/12/1993 (c) 1993 (Japan) (CPS1 + QSound) */
        //TODO 	DRIVER( pnickj )	/* 08/06/1994 (c) 1994 Compile + Capcom license (Japan) not listed on Capcom's site */
        //TODO 	DRIVER( qad )		/* 01/07/1992 (c) 1992 (US)    */
        //TODO 	DRIVER( qadj )		/* 21/09/1994 (c) 1994 (Japan) */
        //TODO 	DRIVER( qtono2 )	/* 23/01/1995 (c) 1995 (Japan) */
        //TODO 	DRIVER( pang3 )		/* 11/05/1995 (c) 1995 Mitchell (Euro) not listed on Capcom's site */
        //TODO 	DRIVER( pang3j )	/* 11/05/1995 (c) 1995 Mitchell (Japan) not listed on Capcom's site */
        //TODO 	DRIVER( megaman )	/* 06/10/1995 (c) 1995 (Asia)  */
        //TODO 	DRIVER( rockmanj )	/* 22/09/1995 (c) 1995 (Japan) */
        //TODO 
        //TODO 	/* Capcom CPS2 games */
        //TODO 	/* list completed by CPS2Shock */
        //TODO 	/* http://cps2shock.retrogames.com */
        //TODO 	DRIVER( ssf2 )		/* 11/09/1993 (c) 1993 (US) */
        //TODO 	DRIVER( ssf2a )		/* 05/10/1993 (c) 1993 (Asia) */
        //TODO 	DRIVER( ssf2ar1 )	/* 14/09/1993 (c) 1993 (Asia) */
        //TODO 	DRIVER( ssf2j )		/* 05/10/1993 (c) 1993 (Japan) */
        //TODO 	DRIVER( ssf2jr1 )	/* 11/09/1993 (c) 1993 (Japan) */
        //TODO 	DRIVER( ssf2jr2 )	/* 10/09/1993 (c) 1993 (Japan) */
        //TODO 	DRIVER( ssf2tb )	/* 11/19/1993 (c) 1993 (World) */
        //TODO 	DRIVER( ssf2tbj )	/* 10/09/1993 (c) 1993 (Japan) */
        //TODO 	DRIVER( ecofghtr )	/* 03/12/1993 (c) 1993 (World) */
        //TODO 	DRIVER( uecology ) 	/* 03/12/1993 (c) 1993 (Japan) */
        //TODO 	DRIVER( ddtod )		/* 12/04/1994 (c) 1993 (Euro) */
        //TODO 	DRIVER( ddtodu )	/* 25/01/1994 (c) 1993 (US) */
        //TODO 	DRIVER( ddtodur1 )	/* 13/01/1994 (c) 1993 (US) */
        //TODO 	DRIVER( ddtodj )	/* 13/01/1994 (c) 1993 (Japan) */
        //TODO 	DRIVER( ddtoda )	/* 13/01/1994 (c) 1993 (Asia) */
        //TODO 	DRIVER( ddtodh )	/* 25/01/1994 (c) 1993 (Hispanic) */
        //TODO 	DRIVER( ssf2t )		/* 23/02/1994 (c) 1994 (World) */
        //TODO 	DRIVER( ssf2tu )	/* 23/02/1994 (c) 1994 (US) */
        //TODO 	DRIVER( ssf2ta )	/* 23/02/1994 (c) 1994 (Asia) */
        //TODO 	DRIVER( ssf2xj )	/* 23/02/1994 (c) 1994 (Japan) */
        //TODO 	DRIVER( avsp )		/* 20/05/1994 (c) 1994 (Euro) */
        //TODO 	DRIVER( avspu )		/* 20/05/1994 (c) 1994 (US) */
        //TODO 	DRIVER( avspj )		/* 20/05/1994 (c) 1994 (Japan) */
        //TODO 	DRIVER( avspa )		/* 20/05/1994 (c) 1994 (Asia) */
        //TODO 						/*    06/1994? Galum Pa! (not listed on Capcom's site) */
        //TODO 	DRIVER( dstlk )		/* 05/07/1994 (c) 1994 (Euro) */
        //TODO 	DRIVER( dstlku )	/* 18/08/1994 (c) 1994 (US) */
        //TODO 	DRIVER( dstlkur1 )	/* 05/07/1994 (c) 1994 (US) */
        //TODO 	DRIVER( dstlka )	/* 05/07/1994 (c) 1994 (Asia) */
        //TODO 	DRIVER( vampj )		/* 05/07/1994 (c) 1994 (Japan) */
        //TODO 	DRIVER( vampja )	/* 05/07/1994 (c) 1994 (Japan) */
        //TODO 	DRIVER( vampjr1 )	/* 30/06/1994 (c) 1994 (Japan) */
        //TODO 	DRIVER( ringdest )	/* 02/09/1994 (c) 1994 (Euro) */
        //TODO 	DRIVER( smbomb )	/* 31/08/1994 (c) 1994 (Japan) */
        //TODO 	DRIVER( smbombr1 )	/* 08/08/1994 (c) 1994 (Japan) */
        //TODO 	DRIVER( armwar )	/* 11/10/1994 (c) 1994 (Euro) */
        //TODO 	DRIVER( armwaru )	/* 24/10/1994 (c) 1994 (US) */
        //TODO 	DRIVER( pgear )		/* 24/10/1994 (c) 1994 (Japan) */
        //TODO 	DRIVER( pgearr1 )	/* 16/09/1994 (c) 1994 (Japan) */
        //TODO 	DRIVER( armwara )	/* 20/09/1994 (c) 1994 (Asia) */
        //TODO 	DRIVER( xmcota )	/* 05/01/1995 (c) 1994 (Euro) */
        //TODO 	DRIVER( xmcotau )	/* 05/01/1995 (c) 1994 (US) */
        //TODO 	DRIVER( xmcotah )	/* 31/03/1995 (c) 1994 (Hispanic) */
        //TODO 	DRIVER( xmcotaj )	/* 19/12/1994 (c) 1994 (Japan) */
        //TODO 	DRIVER( xmcotaj1 )	/* 17/12/1994 (c) 1994 (Japan) */
        //TODO 	DRIVER( xmcotaa )	/* 17/12/1994 (c) 1994 (Asia) */
        //TODO 	DRIVER( xmcotajr )	/* 08/12/1994 (c) 1994 (Japan Rent) */
        //TODO 	DRIVER( nwarr )		/* 06/04/1995 (c) 1995 (US) */
        //TODO 	DRIVER( nwarrh )	/* 03/04/1995 (c) 1995 (Hispanic) */
        //TODO 	DRIVER( vhuntj )	/* 02/03/1995 (c) 1995 (Japan) */
        //TODO 	DRIVER( vhuntjr1 )	/* 02/03/1995 (c) 1995 (Japan) */
        //TODO 	DRIVER( cybots )	/* 24/04/1995 (c) 1995 (US) */
        //TODO 	DRIVER( cybotsj )	/* 20/04/1995 (c) 1995 (Japan) */
        //TODO 	DRIVER( sfa )		/* 27/07/1995 (c) 1995 (Euro) */
        //TODO 	DRIVER( sfar1 )		/* 18/07/1995 (c) 1995 (Euro) */
        //TODO 	DRIVER( sfar2 )		/* 05/06/1995 (c) 1995 (Euro) */
        //TODO 	DRIVER( sfau )		/* 27/06/1995 (c) 1995 (US) */
        //TODO 	DRIVER( sfzj )		/* 27/07/1995 (c) 1995 (Japan) */
        //TODO 	DRIVER( sfzjr1 )	/* 27/06/1995 (c) 1995 (Japan) */
        //TODO 	DRIVER( sfzjr2 )	/* 05/06/1995 (c) 1995 (Japan) */
        //TODO 	DRIVER( sfzh )		/* 27/06/1995 (c) 1995 (Hispanic) */
        //TODO TESTDRIVER( rckmanj )	/* 22/09/1995 Rockman: The Power Battle (Japan) */
        //TODO 	DRIVER( msh )		/* 24/10/1995 (c) 1995 (US) */
        //TODO 	DRIVER( mshj )		/* 24/10/1995 (c) 1995 (Japan) */
        //TODO 	DRIVER( msha )		/* 24/10/1995 (c) 1995 (Asia) */
        //TODO 	DRIVER( mshh )		/* 17/11/1995 (c) 1996 (Hispanic) */
        //TODO 	DRIVER( 19xx )		/* 07/12/1995 (c) 1996 (US) */
        //TODO 	DRIVER( 19xxj )		/* 25/12/1995 (c) 1996 (Japan) */
        //TODO 	DRIVER( 19xxjr1 )	/* 07/12/1995 (c) 1996 (Japan) */
        //TODO 	DRIVER( 19xxh )		/* 18/12/1995 (c) 1996 (Hispanic) */
        //TODO 	DRIVER( ddsom )		/* 09/02/1996 (c) 1996 (Euro) */
        //TODO 	DRIVER( ddsomu )	/* 19/06/1996 (c) 1996 (US) */
        //TODO 	DRIVER( ddsomur1 )	/* 09/02/1996 (c) 1996 (US) */
        //TODO 	DRIVER( ddsomjr1 )	/* 06/02/1996 (c) 1996 (Japan) */
        //TODO 	DRIVER( ddsomj )	/* 19/06/1996 (c) 1996 (Japan) */
        //TODO 	DRIVER( ddsoma )	/* 19/06/1996 (c) 1996 (Asia) */
        //TODO 	DRIVER( sfa2 )		/* 06/03/1996 (c) 1996 (US) */
        //TODO 	DRIVER( sfz2j )		/* 27/02/1996 (c) 1996 (Japan) */
        //TODO 	DRIVER( sfz2a )		/* 27/02/1996 (c) 1996 (Asia) */
        //TODO 	DRIVER( spf2t )		/* 20/06/1996 (c) 1996 (US) */
        //TODO 	DRIVER( spf2xj )	/* 31/05/1996 (c) 1996 (Japan) */
        //TODO 	DRIVER( qndream )	/* 26/06/1996 (c) 1996 (Japan) */
        //TODO 	DRIVER( megaman2 )	/* 08/07/1996 (c) 1996 (US) */
        //TODO 	DRIVER( rckman2j )	/* 08/07/1996 (c) 1996 (Japan) */
        //TODO 	DRIVER( sfz2aj )	/* 05/08/1996 (c) 1996 (Japan) */
        //TODO 	DRIVER( sfz2ah )	/* 13/08/1996 (c) 1996 (Hispanic) */
        //TODO 	DRIVER( sfz2aa )	/* 26/08/1996 (c) 1996 (Asia) */
        //TODO 	DRIVER( xmvsf )		/* 10/09/1996 (c) 1996 (Euro) */
        //TODO 	DRIVER( xmvsfu )	/* 04/10/1996 (c) 1996 (US) */
        //TODO 	DRIVER( xmvsfj )	/* 10/09/1996 (c) 1996 (Japan) */
        //TODO 	DRIVER( xmvsfjr1 )	/* 09/09/1996 (c) 1996 (Japan) */
        //TODO 	DRIVER( xmvsfa )	/* 23/10/1996 (c) 1996 (Asia) */
        //TODO 	DRIVER( xmvsfh )	/* 04/10/1996 (c) 1996 (Hispanic) */
        //TODO 	DRIVER( batcir )	/* 19/03/1997 (c) 1997 (Euro) */
        //TODO 	DRIVER( batcirj )	/* 19/03/1997 (c) 1997 (Japan) */
        //TODO 	DRIVER( batcira )	/* 19/03/1997 (c) 1997 (Asia) */
        //TODO 	DRIVER( vsav )		/* 19/05/1997 (c) 1997 (Euro) */
        //TODO 	DRIVER( vsavu )		/* 19/05/1997 (c) 1997 (US) */
        //TODO 	DRIVER( vsavj )		/* 19/05/1997 (c) 1997 (Japan) */
        //TODO 	DRIVER( vsava )		/* 19/05/1997 (c) 1997 (Asia) */
        //TODO 	DRIVER( vsavh )		/* 19/05/1997 (c) 1997 (Hispanic) */
        //TODO 	DRIVER( mshvsf )	/* 27/08/1997 (c) 1997 (US) */
        //TODO 	DRIVER( mshvsfu1 )	/* 25/06/1997 (c) 1997 (US) */
        //TODO 	DRIVER( mshvsfj )	/* 07/07/1997 (c) 1997 (Japan) */
        //TODO 	DRIVER( mshvsfj1 )	/* 02/07/1997 (c) 1997 (Japan) */
        //TODO 	DRIVER( mshvsfh )	/* 25/06/1997 (c) 1997 (Hispanic) */
        //TODO 	DRIVER( mshvsfa )	/* 25/06/1997 (c) 1997 (Asia) */
        //TODO 	DRIVER( mshvsfa1 )	/* 20/06/1997 (c) 1997 (Asia) */
        //TODO 	DRIVER( csclubj )	/* 22/07/1997 (c) 1997 (Japan) */
        //TODO 	DRIVER( cscluba )	/* 22/07/1997 (c) 1997 (Asia) */
        //TODO 	DRIVER( sgemf )		/* 04/09/1997 (c) 1997 (US) */
        //TODO 	DRIVER( pfghtj )	/* 04/09/1997 (c) 1997 (Japan) */
        //TODO 	DRIVER( sgemfa )	/* 04/09/1997 (c) 1997 (Asia) */
        //TODO 	DRIVER( sgemfh )	/* 04/09/1997 (c) 1997 (Hispanic) */
        //TODO 	DRIVER( vhunt2 )	/* 13/09/1997 (c) 1997 (Japan) */
        //TODO 	DRIVER( vsav2 )		/* 13/09/1997 (c) 1997 (Japan) */
        //TODO 	DRIVER( mvsc )		/* 23/01/1998 (c) 1998 (US) */
        //TODO 	DRIVER( mvscj )		/* 23/01/1998 (c) 1998 (Japan) */
        //TODO 	DRIVER( mvscjr1 )	/* 12/01/1998 (c) 1998 (Japan) */
        //TODO 	DRIVER( mvsca )		/* 12/01/1998 (c) 1998 (Asia) */
        //TODO 	DRIVER( mvsch )		/* 23/01/1998 (c) 1998 (Hispanic) */
        //TODO 	DRIVER( sfa3 )		/* 04/09/1998 (c) 1998 (US) */
        //TODO 	DRIVER( sfa3r1 )	/* 29/06/1998 (c) 1998 (US) */
        //TODO 	DRIVER( sfz3j )		/* 27/07/1998 (c) 1998 (Japan) */
        //TODO 	DRIVER( sfz3jr1 )	/* 29/06/1998 (c) 1998 (Japan) */
        //TODO 	DRIVER( sfz3a )		/* 01/07/1998 (c) 1998 (Asia) */
        //TODO 	DRIVER( gigawing )	/* 22/02/1999 (c) 1999 Takumi (US) */
        //TODO 	DRIVER( gwingj )	/* 23/02/1999 (c) 1999 Takumi (Japan) */
        //TODO TESTDRIVER( jgokushi )	/* 27/05/1999 Jyangokushi: Haoh no Saihai (Japan) */
        //TODO 	DRIVER( dimahoo )	/* 21/01/2000 (c) 2000 Eighting/Raizing (US) */
        //TODO 	DRIVER( gmahou )	/* 21/01/2000 (c) 2000 Eighting/Raizing (Japan) */
        //TODO 	DRIVER( mmatrix )	/* 12/04/2000 (c) 2000 Takumi (US) */
        //TODO 	DRIVER( mmatrixj )	/* 12/04/2000 (c) 2000 Takumi (Japan) */
        //TODO 	DRIVER( 1944 )		/* 20/06/2000 (c) 2000 Eighting/Raizing (US) */
        //TODO 	DRIVER( 1944j )		/* 20/06/2000 (c) 2000 Eighting/Raizing (Japan) */
        //TODO TESTDRIVER( mpangj )	/* 11/10/2000 Mighty! Pang (Mitchell) */
        //TODO TESTDRIVER( progear )	/* 17/01/2001 Progear No Arashi (Cave) */
        //TODO TESTDRIVER( puzloop2 )	/* 05/02/2001 Puzz Loop 2 (Mitchell) */
        //TODO TESTDRIVER( choko )		/* 20/08/2001 Choko (Mitchell) */
        //TODO 
        //TODO #ifndef CPSMAME
        //TODO 
        //TODO 	/* Capcom CPS3 games */
        //TODO 	/* 10/1996 Warzard */
        //TODO 	/*  2/1997 Street Fighter III - New Generation */
        //TODO 	/* ???? Jojo's Bizarre Adventure */
        //TODO 	/* ???? Street Fighter 3: Second Impact ~giant attack~ */
        //TODO 	/* ???? Street Fighter 3: Third Strike ~fight to the finish~ */
        //TODO 
        //TODO 	/* Capcom ZN1 */
        //TODO TESTDRIVER( ts2j )		/*  Battle Arena Toshinden 2 (JAPAN 951124) */
        //TODO 						/*  7/1996 Star Gladiator */
        //TODO TESTDRIVER( sfex )		/*  Street Fighter EX (ASIA 961219) */
        //TODO TESTDRIVER( sfexj )		/*  Street Fighter EX (JAPAN 961130) */
        //TODO TESTDRIVER( sfexp )		/*  Street Fighter EX Plus (USA 970311) */
        //TODO TESTDRIVER( sfexpj )	/*  Street Fighter EX Plus (JAPAN 970311) */
        //TODO TESTDRIVER( rvschool )	/*  Rival Schools (ASIA 971117) */
        //TODO TESTDRIVER( jgakuen )	/*  Justice Gakuen (JAPAN 971117) */
        //TODO 
        //TODO 	/* Capcom ZN2 */
        //TODO TESTDRIVER( sfex2 )		/*  Street Fighter EX 2 (JAPAN 980312) */
        //TODO TESTDRIVER( tgmj )		/*  Tetris The Grand Master (JAPAN 980710) */
        //TODO TESTDRIVER( kikaioh )	/*  Kikaioh (JAPAN 980914) */
        //TODO TESTDRIVER( sfex2p )	/*  Street Fighter EX 2 Plus (JAPAN 990611) */
        //TODO TESTDRIVER( shiryu2 )	/*  Strider Hiryu 2 (JAPAN 991213) */
        //TODO 						/*  Star Gladiator 2 */
        //TODO 						/*  Rival Schools 2 */
        //TODO 
        //TODO 	/* Video System ZN1 */
        //TODO TESTDRIVER( sncwgltd )	/*  Sonic Wings Limited (JAPAN) */
        //TODO 
        //TODO 	/* Tecmo ZN1 */
        //TODO TESTDRIVER( glpracr2 )	/*  Gallop Racer 2 (JAPAN) */
        //TODO TESTDRIVER( doapp )		/*  Dead Or Alive ++ (JAPAN) */
        //TODO 
        //TODO 	/* Mitchell games */
        //TODO 	DRIVER( mgakuen )	/* (c) 1988 Yuga */
        //TODO 	DRIVER( 7toitsu )	/* (c) 1988 Yuga */
        //TODO 	DRIVER( mgakuen2 )	/* (c) 1989 Face */
        //TODO 	DRIVER( pkladies )	/* (c) 1989 Mitchell */
        //TODO 	DRIVER( pkladiel )	/* (c) 1989 Leprechaun */
        //TODO 	DRIVER( dokaben )	/*  3/1989 (c) 1989 Capcom (Japan) */
        //TODO 	/*  8/1989 Dokaben 2 (baseball) */
        //TODO 	DRIVER( pang )		/* (c) 1989 Mitchell (World) */
        //TODO 	DRIVER( pangb )		/* bootleg */
        //TODO 	DRIVER( bbros )		/* (c) 1989 Capcom (US) not listed on Capcom's site */
        //TODO 	DRIVER( pompingw )	/* (c) 1989 Mitchell (Japan) */
        //TODO 	DRIVER( cbasebal )	/* 10/1989 (c) 1989 Capcom (Japan) (different hardware) */
        //TODO 	DRIVER( cworld )	/* 11/1989 (QUIZ 1) (c) 1989 Capcom */
        //TODO 	DRIVER( hatena )	/* 28/02/1990 (QUIZ 2) (c) 1990 Capcom (Japan) */
        //TODO 	DRIVER( spang )		/* 14/09/1990 (c) 1990 Mitchell (World) */
        //TODO 	DRIVER( sbbros )	/* 01/10/1990 (c) 1990 Mitchell + Capcom (US) not listed on Capcom's site */
        //TODO 	DRIVER( marukin )	/* 17/10/1990 (c) 1990 Yuga (Japan) */
        //TODO 	DRIVER( qtono1 )	/* 25/12/1990 (QUIZ 3) (c) 1991 Capcom (Japan) */
        //TODO 	/*  4/1991 Ashita Tenki ni Naare (golf) */
        //TODO 	DRIVER( qsangoku )	/* 07/06/1991 (QUIZ 4) (c) 1991 Capcom (Japan) */
        //TODO 	DRIVER( block )		/* 06/11/1991 (c) 1991 Capcom (World) */
        //TODO 	DRIVER( blocka )	/* 10/09/1991 (c) 1991 Capcom (World) */
        //TODO 	DRIVER( blockj )	/* 10/09/1991 (c) 1991 Capcom (Japan) */
        //TODO 	DRIVER( blockbl )	/* bootleg */
        //TODO 
        //TODO 	/* Incredible Technologies games */
        //TODO 	/* http://www.itsgames.com */
        //TODO 	DRIVER( capbowl )	/* (c) 1988 Incredible Technologies */
        //TODO 	DRIVER( capbowl2 )	/* (c) 1988 Incredible Technologies */
        //TODO 	DRIVER( clbowl )	/* (c) 1989 Incredible Technologies */
        //TODO 	DRIVER( bowlrama )	/* (c) 1991 P & P Marketing */
        //TODO 	DRIVER( wfortune )	/* (c) 1989 GameTek */
        //TODO 	DRIVER( wfortuna )	/* (c) 1989 GameTek */
        //TODO 	DRIVER( stratab )	/* (c) 1990 Strata/Incredible Technologies */
        //TODO 	DRIVER( stratab1 )	/* (c) 1990 Strata/Incredible Technologies */
        //TODO TESTDRIVER( sstrike )	/* (c) 1990 Strata/Incredible Technologies */
        //TODO 	DRIVER( gtg )		/* (c) 1990 Strata/Incredible Technologies */
        //TODO 	DRIVER( hstennis )	/* (c) 1990 Strata/Incredible Technologies */
        //TODO 	DRIVER( hstenn10 )	/* (c) 1990 Strata/Incredible Technologies */
        //TODO 	DRIVER( slikshot )	/* (c) 1990 Grand Products/Incredible Technologies */
        //TODO 	DRIVER( sliksh17 )	/* (c) 1990 Grand Products/Incredible Technologies */
        //TODO TESTDRIVER( dynobop )	/* (c) 1990 Grand Products/Incredible Technologies */
        //TODO 	DRIVER( arlingtn )	/* (c) 1991 Strata/Incredible Technologies */
        //TODO 	DRIVER( peggle )	/* (c) 1991 Strata/Incredible Technologies */
        //TODO 	DRIVER( pegglet )	/* (c) 1991 Strata/Incredible Technologies */
        //TODO 	DRIVER( rimrockn )	/* (c) 1991 Strata/Incredible Technologies */
        //TODO 	DRIVER( rimrck20 )	/* (c) 1991 Strata/Incredible Technologies */
        //TODO 	DRIVER( rimrck16 )	/* (c) 1991 Strata/Incredible Technologies */
        //TODO 	DRIVER( rimrck12 )	/* (c) 1991 Strata/Incredible Technologies */
        //TODO 	DRIVER( ninclown )	/* (c) 1991 Strata/Incredible Technologies */
        //TODO 	DRIVER( gtg2 )		/* (c) 1992 Strata/Incredible Technologies */
        //TODO 	DRIVER( gtg2t )		/* (c) 1989 Strata/Incredible Technologies */
        //TODO 	DRIVER( gtg2j )		/* (c) 1991 Strata/Incredible Technologies */
        //TODO 	DRIVER( neckneck )	/* (c) 1992 Bundra Games/Incredible Technologies */
        //TODO 	DRIVER( timekill )	/* (c) 1992 Strata/Incredible Technologies */
        //TODO 	DRIVER( timek131 )	/* (c) 1992 Strata/Incredible Technologies */
        //TODO 	DRIVER( hardyard )	/* (c) 1993 Strata/Incredible Technologies */
        //TODO 	DRIVER( hardyd10 )	/* (c) 1993 Strata/Incredible Technologies */
        //TODO 	DRIVER( bloodstm )	/* (c) 1994 Strata/Incredible Technologies */
        //TODO 	DRIVER( bloods22 )	/* (c) 1994 Strata/Incredible Technologies */
        //TODO 	DRIVER( bloods21 )	/* (c) 1994 Strata/Incredible Technologies */
        //TODO 	DRIVER( bloods11 )	/* (c) 1994 Strata/Incredible Technologies */
        //TODO 	DRIVER( pairs )		/* (c) 1994 Strata/Incredible Technologies */
        //TODO TESTDRIVER( drivedge )	/* (c) 1994 Strata/Incredible Technologies */
        //TODO 	DRIVER( wcbowl )	/* (c) 1995 Incredible Technologies */
        //TODO 	DRIVER( sftm )		/* (c) 1995 Capcom/Incredible Technologies */
        //TODO 	DRIVER( sftm110 )	/* (c) 1995 Capcom/Incredible Technologies */
        //TODO 	DRIVER( sftmj )		/* (c) 1995 Capcom/Incredible Technologies */
        //TODO 	DRIVER( shufshot )	/* (c) Strata/Incredible Technologies */
        //TODO 
        //TODO 	/* Leland games */
        //TODO 	DRIVER( cerberus )	/* (c) 1985 Cinematronics */
        //TODO 	DRIVER( mayhem )	/* (c) 1985 Cinematronics */
        //TODO 	DRIVER( powrplay )	/* (c) 1985 Cinematronics */
        //TODO 	DRIVER( wseries )	/* (c) 1985 Cinematronics */
        //TODO 	DRIVER( alleymas )	/* (c) 1986 Cinematronics */
        //TODO 	DRIVER( dangerz )	/* (c) 1986 Cinematronics USA */
        //TODO 	DRIVER( basebal2 )	/* (c) 1987 Cinematronics */
        //TODO 	DRIVER( dblplay )	/* (c) 1987 Tradewest / Leland */
        //TODO 	DRIVER( strkzone )	/* (c) 1988 Leland */
        //TODO 	DRIVER( redlin2p )	/* (c) 1987 Cinematronics + Tradewest license */
        //TODO 	DRIVER( quarterb )	/* (c) 1987 Leland */
        //TODO 	DRIVER( quartrba )	/* (c) 1987 Leland */
        //TODO 	DRIVER( viper )		/* (c) 1988 Leland */
        //TODO 	DRIVER( teamqb )	/* (c) 1988 Leland */
        //TODO 	DRIVER( teamqb2 )	/* (c) 1988 Leland */
        //TODO 	DRIVER( aafb )		/* (c) 1989 Leland */
        //TODO 	DRIVER( aafbd2p )	/* (c) 1989 Leland */
        //TODO 	DRIVER( aafbc )		/* (c) 1989 Leland */
        //TODO 	DRIVER( aafbb )		/* (c) 1989 Leland */
        //TODO 	DRIVER( offroad )	/* (c) 1989 Leland */
        //TODO 	DRIVER( offroadt )	/* (c) 1989 Leland */
        //TODO 	DRIVER( pigout )	/* (c) 1990 Leland */
        //TODO 	DRIVER( pigouta )	/* (c) 1990 Leland */
        //TODO 	DRIVER( ataxx )		/* (c) 1990 Leland */
        //TODO 	DRIVER( ataxxa )	/* (c) 1990 Leland */
        //TODO 	DRIVER( ataxxj )	/* (c) 1990 Leland */
        //TODO 	DRIVER( wsf )		/* (c) 1990 Leland */
        //TODO 	DRIVER( indyheat )	/* (c) 1991 Leland */
        //TODO 	DRIVER( brutforc )	/* (c) 1991 Leland */
        //TODO 	DRIVER( asylum )	/* (c) 1991 Leland */
        //TODO 
        //TODO 	/* Gremlin 8080 games */
        //TODO 	/* the numbers listed are the range of ROM part numbers */
        //TODO 	DRIVER( blockade )	/* 1-4 [1977 Gremlin] */
        //TODO 	DRIVER( comotion )	/* 5-7 [1977 Gremlin] */
        //TODO 	DRIVER( hustle )	/* 16-21 [1977 Gremlin] */
        //TODO 	DRIVER( blasto )	/* [1978 Gremlin] */
        //TODO 	DRIVER( mineswpr )	/* [1977 Amutech] */
        //TODO 
        //TODO 	/* Gremlin/Sega "VIC dual game board" games */
        //TODO 	/* the numbers listed are the range of ROM part numbers */
        //TODO 	DRIVER( depthch )	/* 50-55 [1977 Gremlin?] */
        //TODO 	DRIVER( safari )	/* 57-66 [1977 Gremlin?] */
        //TODO 	DRIVER( frogs )		/* 112-119 [1978 Gremlin?] */
        //TODO 	DRIVER( sspaceat )	/* 155-162 (c) */
        //TODO 	DRIVER( sspacat2 )
        //TODO 	DRIVER( sspacatc )	/* 139-146 (c) */
        //TODO 	DRIVER( headon )	/* 163-167/192-193 (c) Gremlin */
        //TODO 	DRIVER( headonb )	/* 163-167/192-193 (c) Gremlin */
        //TODO 	DRIVER( headon2 )	/* ???-??? (c) 1979 Sega */
        //TODO 	/* ???-??? Fortress */
        //TODO 	/* ???-??? Gee Bee */
        //TODO 	/* 255-270  Head On 2 / Deep Scan */
        //TODO 	DRIVER( invho2 )	/* 271-286 (c) 1979 Sega */
        //TODO 	DRIVER( samurai )	/* 289-302 + upgrades (c) 1980 Sega */
        //TODO 	DRIVER( invinco )	/* 310-318 (c) 1979 Sega */
        //TODO 	DRIVER( invds )		/* 367-382 (c) 1979 Sega */
        //TODO 	DRIVER( tranqgun )	/* 413-428 (c) 1980 Sega */
        //TODO 	/* 450-465  Tranquilizer Gun (different version?) */
        //TODO 	/* ???-??? Car Hunt / Deep Scan */
        //TODO 	DRIVER( spacetrk )	/* 630-645 (c) 1980 Sega */
        //TODO 	DRIVER( sptrekct )	/* (c) 1980 Sega */
        //TODO 	DRIVER( carnival )	/* 651-666 (c) 1980 Sega */
        //TODO 	DRIVER( carnvckt )	/* 501-516 (c) 1980 Sega */
        //TODO 	DRIVER( digger )	/* 684-691 no copyright notice */
        //TODO 	DRIVER( pulsar )	/* 790-805 (c) 1981 Sega */
        //TODO 	DRIVER( heiankyo )	/* (c) [1979?] Denki Onkyo */
        //TODO 	DRIVER( alphaho )	/* Data East */
        //TODO 
        //TODO 	/* Sega G-80 vector games */
        //TODO 	DRIVER( spacfury )	/* (c) 1981 */
        //TODO 	DRIVER( spacfura )	/* no copyright notice */
        //TODO 	DRIVER( zektor )	/* (c) 1982 */
        //TODO 	DRIVER( tacscan )	/* (c) */
        //TODO 	DRIVER( elim2 )		/* (c) 1981 Gremlin */
        //TODO 	DRIVER( elim2a )	/* (c) 1981 Gremlin */
        //TODO 	DRIVER( elim4 )		/* (c) 1981 Gremlin */
        //TODO 	DRIVER( startrek )	/* (c) 1982 */
        //TODO 
        //TODO 	/* Sega G-80 raster games */
        //TODO 	DRIVER( astrob )	/* (c) 1981 */
        //TODO 	DRIVER( astrob2 )	/* (c) 1981 */
        //TODO 	DRIVER( astrob1 )	/* (c) 1981 */
        //TODO 	DRIVER( 005 )		/* (c) 1981 */
        //TODO 	DRIVER( monsterb )	/* (c) 1982 */
        //TODO 	DRIVER( spaceod )	/* (c) 1981 */
        //TODO 	DRIVER( pignewt )	/* (c) 1983 */
        //TODO 	DRIVER( pignewta )	/* (c) 1983 */
        //TODO 	DRIVER( sindbadm )	/* 834-5244 (c) 1983 Sega */
        //TODO 
        //TODO 	/* Sega "Zaxxon hardware" games */
        //TODO 	DRIVER( zaxxon )	/* (c) 1982 */
        //TODO 	DRIVER( zaxxon2 )	/* (c) 1982 */
        //TODO 	DRIVER( zaxxonb )	/* bootleg */
        //TODO 	DRIVER( szaxxon )	/* (c) 1982 */
        //TODO 	DRIVER( futspy )	/* (c) 1984 */
        //TODO 	DRIVER( razmataz )	/* modified 834-0213, 834-0214 (c) 1983 */
        //TODO 	DRIVER( ixion )		/* (c) 1983 */
        //TODO 	DRIVER( congo )		/* 605-5167 (c) 1983 */
        //TODO 	DRIVER( tiptop )	/* 605-5167 (c) 1983 */
        //TODO 
        //TODO 	/* Sega System 1 / System 2 games */
        //TODO 	DRIVER( starjack )	/* 834-5191 (c) 1983 (S1) */
        //TODO 	DRIVER( starjacs )	/* (c) 1983 Stern (S1) */
        //TODO 	DRIVER( regulus )	/* 834-5328 (c) 1983 (S1) */
        //TODO 	DRIVER( reguluso )	/* 834-5328 (c) 1983 (S1) */
        //TODO 	DRIVER( regulusu )	/* 834-5328 (c) 1983 (S1) */
        //TODO 	DRIVER( upndown )	/* (c) 1983 (S1) */
        //TODO 	DRIVER( upndownu )	/* (c) 1983 (S1) */
        //TODO 	DRIVER( mrviking )	/* 834-5383 (c) 1984 (S1) */
        //TODO 	DRIVER( mrvikngj )	/* 834-5383 (c) 1984 (S1) */
        //TODO 	DRIVER( swat )		/* 834-5388 (c) 1984 Coreland / Sega (S1) */
        //TODO 	DRIVER( flicky )	/* (c) 1984 (S1) */
        //TODO 	DRIVER( flickyo )	/* (c) 1984 (S1) */
        //TODO 	DRIVER( wmatch )	/* (c) 1984 (S1) */
        //TODO 	DRIVER( bullfgt )	/* 834-5478 (c) 1984 Sega / Coreland (S1) */
        //TODO 	DRIVER( thetogyu )	/* 834-5478 (c) 1984 Sega / Coreland (S1) */
        //TODO 	DRIVER( spatter )	/* 834-5583 (c) 1984 (S1) */
        //TODO 	DRIVER( ssanchan )	/* 834-5583 (c) 1984 (S1) */
        //TODO 	DRIVER( pitfall2 )	/* 834-5627 [1985?] reprogrammed, (c) 1984 Activision (S1) */
        //TODO 	DRIVER( pitfallu )	/* 834-5627 [1985?] reprogrammed, (c) 1984 Activision (S1) */
        //TODO 	DRIVER( seganinj )	/* 834-5677 (c) 1985 (S1) */
        //TODO 	DRIVER( seganinu )	/* 834-5677 (c) 1985 (S1) */
        //TODO 	DRIVER( nprinces )	/* 834-5677 (c) 1985 (S1) */
        //TODO 	DRIVER( nprincso )	/* 834-5677 (c) 1985 (S1) */
        //TODO 	DRIVER( nprincsu )	/* 834-5677 (c) 1985 (S1) */
        //TODO 	DRIVER( nprincsb )	/* bootleg? (S1) */
        //TODO 	DRIVER( imsorry )	/* 834-5707 (c) 1985 Coreland / Sega (S1) */
        //TODO 	DRIVER( imsorryj )	/* 834-5707 (c) 1985 Coreland / Sega (S1) */
        //TODO 	DRIVER( teddybb )	/* 834-5712 (c) 1985 (S1) */
        //TODO 	DRIVER( teddybbo )	/* 834-5712 (c) 1985 (S1) */
        //TODO 	DRIVER( hvymetal )	/* 834-5745 (c) 1985 (S2?) */
        //TODO 	DRIVER( myhero )	/* 834-5755 (c) 1985 (S1) */
        //TODO 	DRIVER( sscandal )	/* 834-5755 (c) 1985 Coreland / Sega (S1) */
        //TODO 	DRIVER( myherok )	/* 834-5755 (c) 1985 Coreland / Sega (S1) */
        //TODO TESTDRIVER( shtngmst )	/* 834-5719/5720 (c) 1985 (S2) */
        //TODO 	DRIVER( chplft )	/* 834-5795 (c) 1985, (c) 1982 Dan Gorlin (S2) */
        //TODO 	DRIVER( chplftb )	/* 834-5795 (c) 1985, (c) 1982 Dan Gorlin (S2) */
        //TODO 	DRIVER( chplftbl )	/* bootleg (S2) */
        //TODO 	DRIVER( 4dwarrio )	/* 834-5918 (c) 1985 Coreland / Sega (S1) */
        //TODO 	DRIVER( brain )		/* (c) 1986 Coreland / Sega (S2?) */
        //TODO 	DRIVER( raflesia )	/* 834-5753 (c) 1985 Coreland / Sega (S1) */
        //TODO 	DRIVER( wboy )		/* 834-5984 (c) 1986 + Escape license (S1) */
        //TODO 	DRIVER( wboyo )		/* 834-5984 (c) 1986 + Escape license (S1) */
        //TODO 	DRIVER( wboy2 )		/* 834-5984 (c) 1986 + Escape license (S1) */
        //TODO 	DRIVER( wboy2u )	/* 834-5984 (c) 1986 + Escape license (S1) */
        //TODO 	DRIVER( wboy3 )		/* 834-5984 (c) 1986 + Escape license (S1) */
        //TODO 	DRIVER( wboyu )		/* 834-5753 (? maybe a conversion) (c) 1986 + Escape license (S1) */
        //TODO 	DRIVER( wbdeluxe )	/* (c) 1986 + Escape license (S1) */
        //TODO TESTDRIVER( gardia )	/* 834-6119 (S2?) */
        //TODO TESTDRIVER( gardiab )	/* bootleg */
        //TODO 	DRIVER( noboranb )	/* bootleg */
        //TODO 	DRIVER( blockgal )	/* 834-6303 (S1) */
        //TODO 	DRIVER( blckgalb )	/* bootleg */
        //TODO 	DRIVER( tokisens )	/* (c) 1987 (from a bootleg board) (S2) */
        //TODO 	DRIVER( wbml )		/* bootleg (S2) */
        //TODO 	DRIVER( wbmljo )	/* (c) 1987 Sega/Westone (S2) */
        //TODO 	DRIVER( wbmljb )	/* (c) 1987 Sega/Westone (S2) */
        //TODO 	DRIVER( wbmlb )		/* bootleg? (S2) */
        //TODO TESTDRIVER( dakkochn )	/* 836-6483? (S2) */
        //TODO TESTDRIVER( ufosensi )	/* 834-6659 (S2) */
        //TODO /*
        //TODO other System 1 / System 2 games:
        //TODO 
        //TODO WarBall
        //TODO Sanrin Sanchan
        //TODO DokiDoki Penguin Land *not confirmed
        //TODO */
        //TODO 
        //TODO 	/* Sega System E games (Master System hardware) */
        //TODO 	DRIVER( hangonjr )	/* (c) 1985 */
        //TODO 	DRIVER( transfrm )	/* 834-5803 (c) 1986 */
        //TODO 	DRIVER( astrofl )	/* 834-5803 (c) 1986 */
        //TODO 	DRIVER( ridleofp )	/* (c) 1986 Sega / Nasco */
        //TODO TESTDRIVER( fantzn2 )
        //TODO TESTDRIVER( opaopa )
        //TODO 
        //TODO 	/* other Sega 8-bit games */
        //TODO 	DRIVER( turbo )		/* (c) 1981 Sega */
        //TODO 	DRIVER( turboa )	/* (c) 1981 Sega */
        //TODO 	DRIVER( turbob )	/* (c) 1981 Sega */
        //TODO 	DRIVER( subroc3d )	/* (c) 1982 Sega */
        //TODO 	DRIVER( buckrog )	/* (c) 1982 Sega */
        //TODO 	DRIVER( buckrogn )	/* (c) 1982 Sega */
        //TODO TESTDRIVER( kopunch )	/* 834-0103 (c) 1981 Sega */
        //TODO 	DRIVER( suprloco )	/* (c) 1982 Sega */
        //TODO 	DRIVER( dotrikun )	/* cabinet test board */
        //TODO 	DRIVER( dotriku2 )	/* cabinet test board */
        //TODO TESTDRIVER( spcpostn )	/* (c) 1986 Sega / Nasco" */
        //TODO 	DRIVER( angelkds )	/* 833-6599 (c) 1988 Sega / Nasco? */
        //TODO 
        //TODO 	/* Sega System 16 games */
        //TODO 	// Not working
        //TODO 	DRIVER( alexkidd )	/* (c) 1986 (protected) */
        //TODO 	DRIVER( aliensya )	/* (c) 1987 (protected) */
        //TODO 	DRIVER( aliensyb )	/* (c) 1987 (protected) */
        //TODO 	DRIVER( aliensyj )	/* (c) 1987 (protected. Japan) */
        //TODO 	DRIVER( astorm )	/* (c) 1990 (protected) */
        //TODO 	DRIVER( astorm2p )	/* (c) 1990 (protected 2 Players) */
        //TODO 	DRIVER( auraila )	/* (c) 1990 Sega / Westone (protected) */
        //TODO 	DRIVER( bayrouta )	/* (c) 1989 (protected) */
        //TODO 	DRIVER( bayrtbl1 )	/* (c) 1989 (protected) (bootleg) */
        //TODO 	DRIVER( bayrtbl2 )	/* (c) 1989 (protected) (bootleg) */
        //TODO 	DRIVER( enduror )	/* (c) 1985 (protected) */
        //TODO 	DRIVER( eswat )		/* (c) 1989 (protected) */
        //TODO 	DRIVER( fpoint )	/* (c) 1989 (protected) */
        //TODO 	DRIVER( goldnaxb )	/* (c) 1989 (protected) */
        //TODO 	DRIVER( goldnaxc )	/* (c) 1989 (protected) */
        //TODO 	DRIVER( goldnaxj )	/* (c) 1989 (protected. Japan) */
        //TODO 	DRIVER( jyuohki )	/* (c) 1988 (protected. Altered Beast Japan) */
        //TODO 	DRIVER( moonwalk )	/* (c) 1990 (protected) */
        //TODO 	DRIVER( moonwlka )	/* (c) 1990 (protected) */
        //TODO 	DRIVER( passsht )	/* (protected) */
        //TODO 	DRIVER( sdioj )		/* (c) 1987 (protected. Japan) */
        //TODO 	DRIVER( shangon )	/* (c) 1992 (protected) */
        //TODO 	DRIVER( shinobia )	/* (c) 1987 (protected) */
        //TODO 	DRIVER( shinobib )	/* (c) 1987 (protected) */
        //TODO 	DRIVER( tetris )	/* (c) 1988 (protected) */
        //TODO 	DRIVER( tetrisa )	/* (c) 1988 (protected) */
        //TODO 	DRIVER( wb3a )		/* (c) 1988 Sega / Westone (protected) */
        //TODO 
        //TODO TESTDRIVER( aceattac )	/* (protected) */
        //TODO TESTDRIVER( afighter )	/* (protected) */
        //TODO 	DRIVER( bloxeed )	/* (protected) */
        //TODO TESTDRIVER( cltchitr )	/* (protected) */
        //TODO TESTDRIVER( cotton )	/* (protected) */
        //TODO TESTDRIVER( cottona )	/* (protected) */
        //TODO TESTDRIVER( ddcrew )	/* (protected) */
        //TODO TESTDRIVER( dunkshot )	/* (protected) */
        //TODO TESTDRIVER( exctleag )  /* (protected) */
        //TODO TESTDRIVER( lghost )	/* (protected) */
        //TODO TESTDRIVER( loffire )	/* (protected) */
        //TODO TESTDRIVER( mvp )		/* (protected) */
        //TODO TESTDRIVER( ryukyu )	/* (protected) */
        //TODO TESTDRIVER( suprleag )  /* (protected) */
        //TODO TESTDRIVER( thndrbld )	/* (protected) */
        //TODO TESTDRIVER( thndrbdj )  /* (protected?) */
        //TODO TESTDRIVER( toutrun )	/* (protected) */
        //TODO TESTDRIVER( toutruna )	/* (protected) */
        //TODO 
        //TODO 	// Working
        //TODO 	DRIVER( aburner )	/* (c) 1987 */
        //TODO 	DRIVER( aburner2 )  /* (c) 1987 */
        //TODO 	DRIVER( alexkida )	/* (c) 1986 */
        //TODO 	DRIVER( aliensyn )	/* (c) 1987 */
        //TODO 	DRIVER( altbeas2 )	/* (c) 1988 */
        //TODO 	DRIVER( altbeast )	/* (c) 1988 */
        //TODO 	DRIVER( astormbl )	/* bootleg */
        //TODO 	DRIVER( atomicp )	/* (c) 1990 Philko */
        //TODO 	DRIVER( aurail )	/* (c) 1990 Sega / Westone */
        //TODO 	DRIVER( bayroute )	/* (c) 1989 Sunsoft / Sega */
        //TODO 	DRIVER( bodyslam )	/* (c) 1986 */
        //TODO 	DRIVER( dduxbl )	/* (c) 1989 (Datsu bootleg) */
        //TODO 	DRIVER( dumpmtmt )	/* (c) 1986 (Japan) */
        //TODO 	DRIVER( endurob2 )	/* (c) 1985 (Beta bootleg) */
        //TODO 	DRIVER( endurobl )	/* (c) 1985 (Herb bootleg) */
        //TODO 	DRIVER( eswatbl )	/* (c) 1989 (but bootleg) */
        //TODO 	DRIVER( fantzone )	/* (c) 1986 */
        //TODO 	DRIVER( fantzono )	/* (c) 1986 */
        //TODO 	DRIVER( fpointbl )	/* (c) 1989 (Datsu bootleg) */
        //TODO 	DRIVER( goldnabl )	/* (c) 1989 (bootleg) */
        //TODO 	DRIVER( goldnaxa )	/* (c) 1989 */
        //TODO 	DRIVER( goldnaxe )	/* (c) 1989 */
        //TODO 	DRIVER( hangon )	/* (c) 1985 */
        //TODO 	DRIVER( hwchamp )	/* (c) 1987 */
        //TODO 	DRIVER( mjleague )	/* (c) 1985 */
        //TODO 	DRIVER( moonwlkb )	/* bootleg */
        //TODO 	DRIVER( outrun )	/* (c) 1986 (bootleg)*/
        //TODO 	DRIVER( outruna )	/* (c) 1986 (bootleg) */
        //TODO 	DRIVER( outrunb )	/* (c) 1986 (protected beta bootleg) */
        //TODO 	DRIVER( passht4b )	/* bootleg */
        //TODO 	DRIVER( passshtb )	/* bootleg */
        //TODO 	DRIVER( quartet )	/* (c) 1986 */
        //TODO 	DRIVER( quartet2 )	/* (c) 1986 */
        //TODO 	DRIVER( quartetj )	/* (c) 1986 */
        //TODO 	DRIVER( riotcity )	/* (c) 1991 Sega / Westone */
        //TODO 	DRIVER( sdi )		/* (c) 1987 */
        //TODO 	DRIVER( shangonb )	/* (c) 1992 (but bootleg) */
        //TODO 	DRIVER( sharrier )	/* (c) 1985 */
        //TODO 	DRIVER( shdancbl )	/* (c) 1989 (but bootleg) */
        //TODO 	DRIVER( shdancer )	/* (c) 1989 */
        //TODO 	DRIVER( shdancrj )	/* (c) 1989 */
        //TODO 	DRIVER( shinobi )	/* (c) 1987 */
        //TODO 	DRIVER( shinobl )	/* (c) 1987 (but bootleg) */
        //TODO 	DRIVER( tetrisbl )	/* (c) 1988 (but bootleg) */
        //TODO 	DRIVER( timscanr )	/* (c) 1987 */
        //TODO 	DRIVER( toryumon )	/* (c) 1995 */
        //TODO 	DRIVER( tturf )		/* (c) 1989 Sega / Sunsoft */
        //TODO 	DRIVER( tturfbl )	/* (c) 1989 (Datsu bootleg) */
        //TODO 	DRIVER( tturfu )	/* (c) 1989 Sega / Sunsoft */
        //TODO 	DRIVER( wb3 )		/* (c) 1988 Sega / Westone */
        //TODO 	DRIVER( wb3bl )		/* (c) 1988 Sega / Westone (but bootleg) */
        //TODO 	DRIVER( wrestwar )	/* (c) 1989 */
        //TODO 
        //TODO /*
        //TODO Sega System 24 game list
        //TODO Apr.1988 Hot Rod
        //TODO Oct.1988 Scramble Spirits
        //TODO Nov.1988 Gain Ground
        //TODO Apr.1989 Crack Down
        //TODO Aug.1989 Jumbo Ozaki Super Masters
        //TODO Jun.1990 Bonanza Bros.
        //TODO Dec.1990 Rough Racer
        //TODO Feb.1991 Quiz Syukudai wo Wasuremashita
        //TODO Jul.1991 Dynamic C.C.
        //TODO Dec.1991 Quiz Rouka ni Tattenasai
        //TODO Dec.1992 Tokorosan no MahMahjan
        //TODO May.1993 Quiz Mekurumeku Story
        //TODO May.1994 Tokorosan no MahMahjan 2
        //TODO Sep.1994 Quiz Ghost Hunter
        //TODO */
        //TODO 
        //TODO 	/* Sega System 32 games */
        //TODO 	DRIVER( holo )		/* (c) 1992 (US) */
        //TODO 	DRIVER( svf )		/* (c) 1994 */
        //TODO 	DRIVER( svs )		/* (c) 1994 */
        //TODO 	DRIVER( jleague )	/* (c) 1994 (Japan) */
        //TODO 	DRIVER( brival )	/* (c) 1992 (Japan) */
        //TODO 	DRIVER( radm )
        //TODO 	DRIVER( radr )		/* (c) 1991 */
        //TODO 	DRIVER( f1en )
        //TODO 	DRIVER( alien3 )	/* (c) 1993 */
        //TODO 	DRIVER( sonic )		/* (c) 1992 (Japan) */
        //TODO 	DRIVER( sonicp )	/* (c) 1992 (Japan) */
        //TODO 	DRIVER( jpark )		/* (c) 1994 */
        //TODO 	DRIVER( ga2 )		/* (c) 1992 */
        //TODO 	DRIVER( ga2j )		/* (c) 1992 */
        //TODO 	DRIVER( spidey )	/* (c) 1991 */
        //TODO 	DRIVER( spideyj )	/* (c) 1991 (Japan) */
        //TODO 	DRIVER( arabfgt )	/* (c) 1991 */
        //TODO TESTDRIVER( f1lap )
        //TODO TESTDRIVER( dbzvrvs )
        //TODO TESTDRIVER( darkedge )
        //TODO 
        //TODO 	/* Sega Multi System 32 games */
        //TODO 	DRIVER( orunners )	/* (c) 1992 (US) */
        //TODO 	DRIVER( harddunk )	/* (c) 1994 (Japan) */
        //TODO TESTDRIVER( titlef )
        //TODO 
        //TODO 	/* Sega ST-V games */
        //TODO TESTDRIVER( astrass )
        //TODO TESTDRIVER( bakubaku )
        //TODO TESTDRIVER( colmns97 )
        //TODO TESTDRIVER( cotton2 )
        //TODO TESTDRIVER( cottonbm )
        //TODO TESTDRIVER( decathlt )
        //TODO TESTDRIVER( diehard )
        //TODO TESTDRIVER( dnmtdeka )
        //TODO TESTDRIVER( ejihon )
        //TODO TESTDRIVER( elandore )
        //TODO TESTDRIVER( ffreveng )
        //TODO TESTDRIVER( fhboxers )
        //TODO TESTDRIVER( findlove )
        //TODO TESTDRIVER( finlarch )
        //TODO TESTDRIVER( gaxeduel )
        //TODO TESTDRIVER( grdforce )
        //TODO TESTDRIVER( groovef )
        //TODO TESTDRIVER( hanagumi )
        //TODO TESTDRIVER( introdon )
        //TODO TESTDRIVER( kiwames )
        //TODO TESTDRIVER( maruchan )
        //TODO TESTDRIVER( myfairld )
        //TODO TESTDRIVER( othellos )
        //TODO TESTDRIVER( pblbeach )
        //TODO TESTDRIVER( prikura )
        //TODO TESTDRIVER( puyosun )
        //TODO TESTDRIVER( rsgun )
        //TODO TESTDRIVER( sandor )
        //TODO TESTDRIVER( sassisu )
        //TODO TESTDRIVER( seabass )
        //TODO TESTDRIVER( shanhigw )
        //TODO TESTDRIVER( shienryu )
        //TODO TESTDRIVER( sleague )
        //TODO TESTDRIVER( sokyugrt )
        //TODO TESTDRIVER( sss )
        //TODO TESTDRIVER( suikoenb )
        //TODO TESTDRIVER( twcup98 )
        //TODO TESTDRIVER( vfkids )
        //TODO TESTDRIVER( vfremix )
        //TODO TESTDRIVER( vmahjong )
        //TODO TESTDRIVER( winterht )
        //TODO TESTDRIVER( znpwfv )
        //TODO 
        //TODO 	/* Deniam games */
        //TODO 	/* they run on Sega System 16 video hardware */
        //TODO 	DRIVER( logicpro )	/* (c) 1996 Deniam */
        //TODO 	DRIVER( karianx )	/* (c) 1996 Deniam */
        //TODO 	DRIVER( logicpr2 )	/* (c) 1997 Deniam (Japan) */
        //TODO /*
        //TODO Deniam is a Korean company (http://deniam.co.kr).
        //TODO 
        //TODO Game list:
        //TODO Title            System     Date
        //TODO ---------------- ---------- ----------
        //TODO GO!GO!           deniam-16b 1995/10/11
        //TODO Logic Pro        deniam-16b 1996/10/20
        //TODO Karian Cross     deniam-16b 1997/04/17
        //TODO LOTTERY GAME     deniam-16c 1997/05/21
        //TODO Logic Pro 2      deniam-16c 1997/06/20
        //TODO Propose          deniam-16c 1997/06/21
        //TODO BOMULEUL CHAJARA SEGA ST-V  1997/04/11
        //TODO */
        //TODO 
        //TODO 	/* System C games */
        //TODO 	DRIVER( bloxeedc )	/* (c) 1989 Sega / Elorg*/
        //TODO 	DRIVER( columns )	/* (c) 1990 Sega */
        //TODO 	DRIVER( columnsj )	/* (c) 1990 Sega */
        //TODO 	DRIVER( columns2 )	/* (c) 1990 Sega */
        //TODO 
        //TODO 	/* System C-2 games */
        //TODO 	DRIVER( borench )	/* (c) 1990 Sega */
        //TODO 	DRIVER( tfrceac )	/* (c) 1990 Sega / Technosoft */
        //TODO 	DRIVER( tfrceacj )	/* (c) 1990 Sega / Technosoft */
        //TODO 	DRIVER( tfrceacb )	/* bootleg */
        //TODO 	DRIVER( ribbit )	/* (c) 1991 Sega */
        //TODO 	DRIVER( tantr )		/* (c) 1992 Sega */
        //TODO 	DRIVER( tantrbl )	/* bootleg */
        //TODO 	DRIVER( tantrbl2 )	/* bootleg */
        //TODO 	DRIVER( puyopuyo )	/* (c) 1992 Sega / Compile */
        //TODO 	DRIVER( puyopuya )	/* (c) 1992 Sega / Compile */
        //TODO 	DRIVER( puyopuyb )	/* bootleg */
        //TODO 	DRIVER( ichidant )	/* (c) 1994 Sega */
        //TODO 	DRIVER( ichidnte )	/* (c) 1994 Sega */
        //TODO 	DRIVER( stkclmns )	/* (c) 1994 Sega */
        //TODO 	DRIVER( puyopuy2 )	/* (c) 1994 Compile + Sega license */
        //TODO 	DRIVER( potopoto )	/* (c) 1994 Sega */
        //TODO 	DRIVER( zunkyou )	/* (c) 1994 Sega */
        //TODO 
        //TODO 	/* Data East "Burger Time hardware" games */
        //TODO 	DRIVER( lnc )		/* (c) 1981 */
        //TODO 	DRIVER( zoar )		/* (c) 1982 */
        //TODO 	DRIVER( btime )		/* (c) 1982 */
        //TODO 	DRIVER( btime2 )	/* (c) 1982 */
        //TODO 	DRIVER( btimem )	/* (c) 1982 + Midway */
        //TODO 	DRIVER( cookrace )	/* bootleg */
        //TODO 	DRIVER( wtennis )	/* bootleg 1982 */
        //TODO 	DRIVER( brubber )	/* (c) 1982 */
        //TODO 	DRIVER( bnj )		/* (c) 1982 + Midway */
        //TODO 	DRIVER( caractn )	/* bootleg */
        //TODO 	DRIVER( disco )		/* (c) 1982 */
        //TODO 	DRIVER( discof )	/* (c) 1982 */
        //TODO 	DRIVER( mmonkey )	/* (c) 1982 Technos Japan + Roller Tron */
        //TODO 	/* cassette system, parent is decocass */
        //TODO 	DRIVER( ctsttape )	/* ? */
        //TODO 	DRIVER( cterrani )	/* 04 (c) 1981 */
        //TODO 	DRIVER( castfant )	/* 07 (c) 1981 */
        //TODO 	DRIVER( csuperas )	/* 09 (c) 1981 */
        //TODO 	DRIVER( clocknch )	/* 11 (c) 1981 */
        //TODO 	DRIVER( cprogolf )	/* 13 (c) 1981 */
        //TODO 	DRIVER( cluckypo )	/* 15 (c) 1981 */
        //TODO 	DRIVER( ctisland )	/* 16 (c) 1981 */
        //TODO 	DRIVER( ctislnd2 )	/* 16 (c) 1981 */
        //TODO 	DRIVER( ctislnd3 )	/* 16? (c) 1981 */
        //TODO 	DRIVER( cdiscon1 )	/* 19 (c) 1982 */
        //TODO 	DRIVER( csweetht )	/* ?? (c) 1982, clone of disco no 1 */
        //TODO 	DRIVER( ctornado )	/* 20 (c) 1982 */
        //TODO 	DRIVER( cmissnx )	/* 21 (c) 1982 */
        //TODO 	DRIVER( cptennis )	/* 22 (c) 1982 */
        //TODO 	DRIVER( cexplore )	/* ?? (c) 1982 */
        //TODO 	DRIVER( cbtime )	/* 26 (c) 1982 */
        //TODO 	DRIVER( cburnrub )	/* ?? (c) 1982 */
        //TODO 	DRIVER( cburnrb2 )	/* ?? (c) 1982 */
        //TODO 	DRIVER( cbnj )		/* 27 (c) 1982 */
        //TODO 	DRIVER( cgraplop )	/* 28 (c) 1983 */
        //TODO 	DRIVER( cgraplp2 )	/* 28? (c) 1983 */
        //TODO 	DRIVER( clapapa )	/* 29 (c) 1983 */
        //TODO 	DRIVER( clapapa2 )	/* 29 (c) 1983 */ /* this one doesn't display lapapa anyehere */
        //TODO 	DRIVER( cnightst )	/* 32 (c) 1983 */
        //TODO 	DRIVER( cnights2 )	/* 32 (c) 1983 */
        //TODO 	DRIVER( cprosocc )	/* 33 (c) 1983 */
        //TODO 	DRIVER( cprobowl )	/* ?? (c) 1983 */
        //TODO 	DRIVER( cscrtry )	/* 38 (c) 1984 */
        //TODO 	DRIVER( cscrtry2 )	/* 38 (c) 1984 */
        //TODO 	DRIVER( cppicf )	/* 39 (c) 1984 */
        //TODO 	DRIVER( cppicf2 )	/* 39 (c) 1984 */
        //TODO 	DRIVER( cfghtice )	/* 40 (c) 1984 */
        //TODO 	DRIVER( cbdash )	/* 44 (c) 1985 */
        //TODO 	/* the following don't work at all */
        //TODO TESTDRIVER ( chwy )		/* ?? (c) 198? */
        //TODO TESTDRIVER ( cflyball ) /* ?? (c) 198? */
        //TODO TESTDRIVER ( czeroize ) /* ?? (c) 198? */
        //TODO 
        //TODO 	/* other Data East games */
        //TODO 	DRIVER( astrof )	/* (c) [1980?] */
        //TODO 	DRIVER( astrof2 )	/* (c) [1980?] */
        //TODO 	DRIVER( astrof3 )	/* (c) [1980?] */
        //TODO 	DRIVER( tomahawk )	/* (c) [1980?] */
        //TODO 	DRIVER( tomahaw5 )	/* (c) [1980?] */
        //TODO TESTDRIVER( prosoccr )	/* (c) 1983 */
        //TODO TESTDRIVER( prosport )	/* (c) 1983 */
        //TODO 	DRIVER( boomrang )	/* (c) 1983 */
        //TODO 	DRIVER( kamikcab )	/* (c) 1984 */
        //TODO 	DRIVER( liberate )	/* (c) 1984 */
        //TODO 	DRIVER( liberatb )	/* bootleg */
        //TODO 	DRIVER( kchamp )	/* (c) 1984 Data East USA (US) */
        //TODO 	DRIVER( karatedo )	/* (c) 1984 Data East Corporation (Japan) */
        //TODO 	DRIVER( kchampvs )	/* (c) 1984 Data East USA (US) */
        //TODO 	DRIVER( karatevs )	/* (c) 1984 Data East Corporation (Japan) */
        //TODO 	DRIVER( firetrap )	/* (c) 1986 Data East USA (US) */
        //TODO 	DRIVER( firetpbl )	/* bootleg */
        //TODO 	DRIVER( brkthru )	/* (c) 1986 Data East USA (US) */
        //TODO 	DRIVER( brkthruj )	/* (c) 1986 Data East Corporation (Japan) */
        //TODO 	DRIVER( darwin )	/* (c) 1986 Data East Corporation (Japan) */
        //TODO 	DRIVER( shootout )	/* (c) 1985 Data East USA (US) */
        //TODO 	DRIVER( shootouj )	/* (c) 1985 Data East USA (Japan) */
        //TODO 	DRIVER( shootoub )	/* bootleg */
        //TODO 	DRIVER( sidepckt )	/* (c) 1986 Data East Corporation */
        //TODO 	DRIVER( sidepctj )	/* (c) 1986 Data East Corporation */
        //TODO 	DRIVER( sidepctb )	/* bootleg */
        //TODO 	DRIVER( exprraid )	/* (c) 1986 Data East USA (US) */
        //TODO 	DRIVER( wexpress )	/* (c) 1986 Data East Corporation (World?) */
        //TODO 	DRIVER( wexpresb )	/* bootleg */
        //TODO 	DRIVER( wexpresc )	/* bootleg */
        //TODO 	DRIVER( pcktgal )	/* (c) 1987 Data East Corporation (Japan) */
        //TODO 	DRIVER( pcktgalb )	/* bootleg */
        //TODO 	DRIVER( pcktgal2 )	/* (c) 1989 Data East Corporation (World?) */
        //TODO 	DRIVER( spool3 )	/* (c) 1989 Data East Corporation (World?) */
        //TODO 	DRIVER( spool3i )	/* (c) 1990 Data East Corporation + I-Vics license */
        //TODO 	DRIVER( battlera )	/* (c) 1988 Data East Corporation (World) */
        //TODO 	DRIVER( bldwolf )	/* (c) 1988 Data East USA (US) */
        //TODO 	DRIVER( actfancr )	/* (c) 1989 Data East Corporation (World) */
        //TODO 	DRIVER( actfanc1 )	/* (c) 1989 Data East Corporation (World) */
        //TODO 	DRIVER( actfancj )	/* (c) 1989 Data East Corporation (Japan) */
        //TODO 	DRIVER( triothep )	/* (c) 1989 Data East Corporation (Japan) */
        //TODO 
        //TODO 	/* Data East 8-bit games */
        //TODO 	DRIVER( lastmisn )	/* (c) 1986 Data East USA (US) */
        //TODO 	DRIVER( lastmsno )	/* (c) 1986 Data East USA (US) */
        //TODO 	DRIVER( lastmsnj )	/* (c) 1986 Data East Corporation (Japan) */
        //TODO 	DRIVER( shackled )	/* (c) 1986 Data East USA (US) */
        //TODO 	DRIVER( breywood )	/* (c) 1986 Data East Corporation (Japan) */
        //TODO 	DRIVER( csilver )	/* (c) 1987 Data East Corporation (Japan) */
        //TODO 	DRIVER( ghostb )	/* (c) 1987 Data East USA (US) */
        //TODO 	DRIVER( ghostb3 )	/* (c) 1987 Data East USA (US) */
        //TODO 	DRIVER( meikyuh )	/* (c) 1987 Data East Corporation (Japan) */
        //TODO 	DRIVER( srdarwin )	/* (c) 1987 Data East Corporation (World) */
        //TODO 	DRIVER( srdarwnj )	/* (c) 1987 Data East Corporation (Japan) */
        //TODO 	DRIVER( gondo )		/* (c) 1987 Data East USA (US) */
        //TODO 	DRIVER( makyosen )	/* (c) 1987 Data East Corporation (Japan) */
        //TODO 	DRIVER( garyoret )	/* (c) 1987 Data East Corporation (Japan) */
        //TODO 	DRIVER( cobracom )	/* (c) 1988 Data East Corporation (World) */
        //TODO 	DRIVER( cobracmj )	/* (c) 1988 Data East Corporation (Japan) */
        //TODO 	DRIVER( oscar )		/* (c) 1988 Data East USA (US) */
        //TODO 	DRIVER( oscarj )	/* (c) 1987 Data East Corporation (Japan) */
        //TODO 	DRIVER( oscarj1 )	/* (c) 1987 Data East Corporation (Japan) */
        //TODO 	DRIVER( oscarj0 )	/* (c) 1987 Data East Corporation (Japan) */
        //TODO 
        //TODO 	/* Data East 16-bit games */
        //TODO 	DRIVER( karnov )	/* (c) 1987 Data East USA (US) */
        //TODO 	DRIVER( karnovj )	/* (c) 1987 Data East Corporation (Japan) */
        //TODO 	DRIVER( wndrplnt )	/* (c) 1987 Data East Corporation (Japan) */
        //TODO 	DRIVER( chelnov )	/* (c) 1988 Data East USA (World) */
        //TODO 	DRIVER( chelnovu )	/* (c) 1988 Data East USA (US) */
        //TODO 	DRIVER( chelnovj )	/* (c) 1988 Data East Corporation (Japan) */
        //TODO /* the following ones all run on similar hardware */
        //TODO 	DRIVER( hbarrel )	/* (c) 1987 Data East USA (US) */
        //TODO 	DRIVER( hbarrelw )	/* (c) 1987 Data East Corporation (World) */
        //TODO 	DRIVER( baddudes )	/* (c) 1988 Data East USA (US) */
        //TODO 	DRIVER( drgninja )	/* (c) 1988 Data East Corporation (Japan) */
        //TODO 	DRIVER( birdtry )	/* (c) 1988 Data East Corporation (Japan) */
        //TODO 	DRIVER( robocop )	/* (c) 1988 Data East Corporation (World) */
        //TODO 	DRIVER( robocopw )	/* (c) 1988 Data East Corporation (World) */
        //TODO 	DRIVER( robocopj )	/* (c) 1988 Data East Corporation (Japan) */
        //TODO 	DRIVER( robocopu )	/* (c) 1988 Data East USA (US) */
        //TODO 	DRIVER( robocpu0 )	/* (c) 1988 Data East USA (US) */
        //TODO 	DRIVER( robocopb )	/* bootleg */
        //TODO 	DRIVER( hippodrm )	/* (c) 1989 Data East USA (US) */
        //TODO 	DRIVER( ffantasy )	/* (c) 1989 Data East Corporation (Japan) */
        //TODO 	DRIVER( ffantasa )	/* (c) 1989 Data East Corporation (Japan) */
        //TODO 	DRIVER( slyspy )	/* (c) 1989 Data East USA (US) */
        //TODO 	DRIVER( slyspy2 )	/* (c) 1989 Data East USA (US) */
        //TODO 	DRIVER( secretag )	/* (c) 1989 Data East Corporation (World) */
        //TODO TESTDRIVER( secretab )	/* bootleg */
        //TODO 	DRIVER( midres )	/* (c) 1989 Data East Corporation (World) */
        //TODO 	DRIVER( midresu )	/* (c) 1989 Data East USA (US) */
        //TODO 	DRIVER( midresj )	/* (c) 1989 Data East Corporation (Japan) */
        //TODO 	DRIVER( bouldash )	/* (c) 1990 Data East Corporation (World) */
        //TODO 	DRIVER( bouldshj )	/* (c) 1990 Data East Corporation (Japan) */
        //TODO /* end of similar hardware */
        //TODO 	DRIVER( stadhero )	/* (c) 1988 Data East Corporation (Japan) */
        //TODO 	DRIVER( madmotor )	/* (c) [1989] Mitchell */
        //TODO 	/* All these games have a unique code stamped on the mask roms */
        //TODO 	DRIVER( vaportra )	/* MAA (c) 1989 Data East Corporation (World) */
        //TODO 	DRIVER( vaportru )	/* MAA (c) 1989 Data East Corporation (US) */
        //TODO 	DRIVER( kuhga )		/* MAA (c) 1989 Data East Corporation (Japan) */
        //TODO 	DRIVER( cbuster )	/* MAB (c) 1990 Data East Corporation (World) */
        //TODO 	DRIVER( cbusterw )	/* MAB (c) 1990 Data East Corporation (World) */
        //TODO 	DRIVER( cbusterj )	/* MAB (c) 1990 Data East Corporation (Japan) */
        //TODO 	DRIVER( twocrude )	/* MAB (c) 1990 Data East USA (US) */
        //TODO 	DRIVER( darkseal )	/* MAC (c) 1990 Data East Corporation (World) */
        //TODO 	DRIVER( darksea1 )	/* MAC (c) 1990 Data East Corporation (World) */
        //TODO 	DRIVER( darkseaj )	/* MAC (c) 1990 Data East Corporation (Japan) */
        //TODO 	DRIVER( gatedoom )	/* MAC (c) 1990 Data East Corporation (US) */
        //TODO 	DRIVER( gatedom1 )	/* MAC (c) 1990 Data East Corporation (US) */
        //TODO 	DRIVER( edrandy )	/* MAD (c) 1990 Data East Corporation (World) */
        //TODO 	DRIVER( edrandy1 )	/* MAD (c) 1990 Data East Corporation (World) */
        //TODO 	DRIVER( edrandyj )	/* MAD (c) 1990 Data East Corporation (Japan) */
        //TODO 	DRIVER( supbtime )	/* MAE (c) 1990 Data East Corporation (World) */
        //TODO 	DRIVER( supbtimj )	/* MAE (c) 1990 Data East Corporation (Japan) */
        //TODO 	DRIVER( mutantf )	/* MAF (c) 1992 Data East Corporation (World) */
        //TODO 	DRIVER( mutantfa )	/* MAF (c) 1992 Data East Corporation (World) */
        //TODO 	DRIVER( deathbrd )	/* MAF (c) 1992 Data East Corporation (Japan) */
        //TODO 	DRIVER( cninja )	/* MAG (c) 1991 Data East Corporation (World) */
        //TODO 	DRIVER( cninja0 )	/* MAG (c) 1991 Data East Corporation (World) */
        //TODO 	DRIVER( cninjau )	/* MAG (c) 1991 Data East Corporation (US) */
        //TODO 	DRIVER( joemac )	/* MAG (c) 1991 Data East Corporation (Japan) */
        //TODO 	DRIVER( stoneage )	/* bootleg */
        //TODO 	DRIVER( robocop2 )	/* MAH (c) 1991 Data East Corporation (World) */
        //TODO 	DRIVER( robocp2u )	/* MAH (c) 1991 Data East Corporation (US) */
        //TODO 	DRIVER( robocp2j )	/* MAH (c) 1991 Data East Corporation (Japan) */
        //TODO 	DRIVER( thndzone )	/* MAJ (c) 1991 Data East Corporation (World) */
        //TODO 	DRIVER( dassault )	/* MAJ (c) 1991 Data East Corporation (US) */
        //TODO 	DRIVER( dassaul4 )	/* MAJ (c) 1991 Data East Corporation (US) */
        //TODO 	DRIVER( chinatwn )	/* MAK (c) 1991 Data East Corporation (Japan) */
        //TODO TESTDRIVER( rohga )		/* MAM (c) 1991 Data East Corporation (Asia/Euro) */
        //TODO TESTDRIVER( rohgah )	/* MAM (c) 1991 Data East Corporation (Hong Kong) */
        //TODO TESTDRIVER( rohgau )	/* MAM (c) 1991 Data East Corporation (US) */
        //TODO 	DRIVER( captaven )	/* MAN (c) 1991 Data East Corporation (Asia) */
        //TODO 	DRIVER( captavna )	/* MAN (c) 1991 Data East Corporation (Asia) */
        //TODO 	DRIVER( captavne )	/* MAN (c) 1991 Data East Corporation (UK) */
        //TODO 	DRIVER( captavnu )	/* MAN (c) 1991 Data East Corporation (US) */
        //TODO 	DRIVER( captavuu )	/* MAN (c) 1991 Data East Corporation (US) */
        //TODO 	DRIVER( captavnj )	/* MAN (c) 1991 Data East Corporation (Japan) */
        //TODO 	/* MAO ?? */
        //TODO 	DRIVER( tumblep )	/* MAP (c) 1991 Data East Corporation (World) */
        //TODO 	DRIVER( tumblepj )	/* MAP (c) 1991 Data East Corporation (Japan) */
        //TODO 	DRIVER( tumblepb )	/* bootleg */
        //TODO 	DRIVER( tumblep2 )	/* bootleg */
        //TODO 	DRIVER( jumpkids )	/* (c) 1993 Comad */
        //TODO 	DRIVER( fncywld )	/* (c) 1996 Unico */
        //TODO 	DRIVER( lemmings )	/* prototype (c) 1991 Data East USA (US) */
        //TODO 	/* MAQ ?? */
        //TODO 	DRIVER( dragngun )	/* MAR (c) 1992 Data East Corporation (US) */
        //TODO 	DRIVER( wizdfire )	/* MAS (c) 1992 Data East Corporation (US) */
        //TODO 	DRIVER( darksel2 )	/* MAS (c) 1992 Data East Corporation (Japan) */
        //TODO 	DRIVER( funkyjet )	/* MAT (c) 1992 Mitchell */
        //TODO 	/* MAU ?? */
        //TODO TESTDRIVER( nitrobal )	/* MAV (c) 1992 Data East Corporation (US) */
        //TODO 	/* MAW ?? */
        //TODO 	/* MAX ?? */
        //TODO 	/* Diet GoGo           MAY (c) 1993 */
        //TODO 	/* Pocket Gal DX       MAZ (c) 1993 */
        //TODO 	/* MBA ?? */
        //TODO 	/* MBB ?? */
        //TODO 	/* MBC ?? */
        //TODO 	/* Boogie Wings/The Great Ragtime Show MBD (c) 1993 */
        //TODO 	/* Double Wings        MBE (c) 1993 Mitchell */
        //TODO TESTDRIVER( fghthist )	/* MBF (c) 1993 Data East Corporation (US) */
        //TODO TESTDRIVER( fghthstw )	/* MBF (c) 1993 Data East Corporation (World) */
        //TODO TESTDRIVER( fghthsta )	/* MBF (c) 1993 Data East Corporation (US) */
        //TODO 	/* Heavy Smash         MBG */
        //TODO TESTDRIVER( nslasher )	/* MBH */
        //TODO 	/* MBI ?? */
        //TODO 	/* MBJ ?? */
        //TODO 	/* MBK ?? */
        //TODO 	/* MBL ?? */
        //TODO 	DRIVER( lockload )	/* MBM (c) 1994 Data East Corporation (US) */
        //TODO 	/* Joe & Mac Return    MBN (c) 1994 */
        //TODO 	/* MBO ?? */
        //TODO 	/* MBP ?? */
        //TODO 	/* MBQ ?? */
        //TODO 	DRIVER( tattass )	/* prototype (c) 1994 Data East Pinball (US) */
        //TODO 	DRIVER( tattassa )	/* prototype (c) 1994 Data East Pinball (Asia) */
        //TODO 	/* Charlie Ninja       MBR (c) Mitchell */
        //TODO 	/* MBS ?? */
        //TODO 	/* MBT ?? */
        //TODO 	/* MBU ?? */
        //TODO 	/* MBV ?? */
        //TODO 	/* MBW ?? */
        //TODO 	/* MBX ?? */
        //TODO 	/* MBY ?? */
        //TODO 	/* Backfire            MBZ (c) 1995 */
        //TODO 	/* MCA ?? */
        //TODO 	/* Ganbare! Gonta!! 2/Lady Killer Part 2 - Party Time  MCB (c) 1995 Mitchell */
        //TODO 	/* Chain Reaction      MCC (c) 1994 */
        //TODO 	/* MCD ?? */
        //TODO 	/* Dunk Dream 95/Hoops MCE (c) 1995 */
        //TODO 	/* MCF ?? */
        //TODO TESTDRIVER( avengrgs )	/* MCG (c) 1995 Data East Corporation (Japan) */
        //TODO 	DRIVER( sotsugyo )	/* (c) 1995 Mitchell (Atlus license) */
        //TODO 	DRIVER( sshangha )	/* (c) 1992 Hot-B */
        //TODO 	DRIVER( sshanghb )	/* bootleg */
        //TODO 
        //TODO 	/* Tehkan / Tecmo games (Tehkan became Tecmo in 1986) */
        //TODO 	DRIVER( senjyo )	/* (c) 1983 Tehkan */
        //TODO 	DRIVER( starforc )	/* (c) 1984 Tehkan */
        //TODO 	DRIVER( starfore )	/* (c) 1984 Tehkan */
        //TODO 	DRIVER( megaforc )	/* (c) 1985 Tehkan + Video Ware license */
        //TODO 	DRIVER( baluba )	/* (c) 1986 Able Corp. */
        //TODO 	DRIVER( bombjack )	/* (c) 1984 Tehkan */
        //TODO 	DRIVER( bombjac2 )	/* (c) 1984 Tehkan */
        //TODO 	DRIVER( pbaction )	/* (c) 1985 Tehkan */
        //TODO 	DRIVER( pbactio2 )	/* (c) 1985 Tehkan */
        //TODO 	/* 6009 Tank Busters */
        //TODO 	/* 6011 Pontoon (c) 1985 Tehkan is a gambling game - removed */
        //TODO 	DRIVER( tehkanwc )	/* (c) 1985 Tehkan */
        //TODO 	DRIVER( gridiron )	/* (c) 1985 Tehkan */
        //TODO 	DRIVER( teedoff )	/* 6102 - (c) 1986 Tecmo */
        //TODO 	DRIVER( solomon )	/* (c) 1986 Tecmo */
        //TODO 	DRIVER( rygar )		/* 6002 - (c) 1986 Tecmo */
        //TODO 	DRIVER( rygar2 )	/* 6002 - (c) 1986 Tecmo */
        //TODO 	DRIVER( rygarj )	/* 6002 - (c) 1986 Tecmo */
        //TODO 	DRIVER( gemini )	/* (c) 1987 Tecmo */
        //TODO 	DRIVER( silkworm )	/* 6217 - (c) 1988 Tecmo */
        //TODO 	DRIVER( silkwrm2 )	/* 6217 - (c) 1988 Tecmo */
        //TODO 	DRIVER( tbowl )		/* 6206 - (c) 1987 Tecmo */
        //TODO 	DRIVER( tbowlj )	/* 6206 - (c) 1987 Tecmo */
        //TODO 	DRIVER( shadoww )	/* 6215 - (c) 1988 Tecmo (World) */
        //TODO 	DRIVER( shadowwa )	/* 6215 - (c) 1988 Tecmo (World) */
        //TODO 	DRIVER( gaiden )	/* 6215 - (c) 1988 Tecmo (US) */
        //TODO 	DRIVER( ryukendn )	/* 6215 - (c) 1989 Tecmo (Japan) */
        //TODO 	DRIVER( wildfang )	/* (c) 1989 Tecmo */
        //TODO 	DRIVER( tknight )	/* (c) 1989 Tecmo */
        //TODO 	DRIVER( stratof )	/* (c) 1991 Tecmo */
        //TODO 	DRIVER( raiga )		/* (c) 1991 Tecmo */
        //TODO 	DRIVER( wc90 )		/* (c) 1989 Tecmo */
        //TODO 	DRIVER( wc90a )		/* (c) 1989 Tecmo */
        //TODO TESTDRIVER( wc90t )		/* (c) 1989 Tecmo */
        //TODO 	DRIVER( wc90b )		/* bootleg */
        //TODO 	DRIVER( spbactn )	/* 9002 - (c) 1991 Tecmo */
        //TODO 	DRIVER( fstarfrc )	/* (c) 1992 Tecmo */
        //TODO 	DRIVER( ginkun )	/* (c) 1995 Tecmo */
        //TODO TESTDRIVER( deroon )
        //TODO TESTDRIVER( tkdensho )
        //TODO 
        //TODO 	/* Konami bitmap games */
        //TODO 	DRIVER( tutankhm )	/* GX350 (c) 1982 Konami */
        //TODO 	DRIVER( tutankst )	/* GX350 (c) 1982 Stern */
        //TODO 	DRIVER( junofrst )	/* GX310 (c) 1983 Konami */
        //TODO 	DRIVER( junofstg )	/* GX310 (c) 1983 Konami + Gottlieb license */
        //TODO 
        //TODO 	/* Konami games */
        //TODO 	DRIVER( pooyan )	/* GX320 (c) 1982 */
        //TODO 	DRIVER( pooyans )	/* GX320 (c) 1982 Stern */
        //TODO 	DRIVER( pootan )	/* bootleg */
        //TODO 	DRIVER( timeplt )	/* GX393 (c) 1982 */
        //TODO 	DRIVER( timepltc )	/* GX393 (c) 1982 + Centuri license*/
        //TODO 	DRIVER( spaceplt )	/* bootleg */
        //TODO 	DRIVER( psurge )	/* (c) 1988 unknown (NOT Konami) */
        //TODO 	DRIVER( megazone )	/* GX319 (c) 1983 */
        //TODO 	DRIVER( megaznik )	/* GX319 (c) 1983 + Interlogic / Kosuka */
        //TODO 	DRIVER( pandoras )	/* GX328 (c) 1984 + Interlogic */
        //TODO 	DRIVER( gyruss )	/* GX347 (c) 1983 */
        //TODO 	DRIVER( gyrussce )	/* GX347 (c) 1983 + Centuri license */
        //TODO 	DRIVER( venus )		/* bootleg */
        //TODO 	DRIVER( trackfld )	/* GX361 (c) 1983 */
        //TODO 	DRIVER( trackflc )	/* GX361 (c) 1983 + Centuri license */
        //TODO 	DRIVER( hyprolym )	/* GX361 (c) 1983 */
        //TODO 	DRIVER( hyprolyb )	/* bootleg */
        //TODO TESTDRIVER( whizquiz )	/* (c) 1985 Zilec-Zenitone */
        //TODO 	DRIVER( mastkin )	/* (c) 1988 Du Tech */
        //TODO 	DRIVER( rocnrope )	/* GX364 (c) 1983 */
        //TODO 	DRIVER( rocnropk )	/* GX364 (c) 1983 + Kosuka */
        //TODO 	DRIVER( circusc )	/* GX380 (c) 1984 */
        //TODO 	DRIVER( circusc2 )	/* GX380 (c) 1984 */
        //TODO 	DRIVER( circuscc )	/* GX380 (c) 1984 + Centuri license */
        //TODO 	DRIVER( circusce )	/* GX380 (c) 1984 + Centuri license */
        //TODO 	DRIVER( tp84 )		/* GX388 (c) 1984 */
        //TODO 	DRIVER( tp84a )		/* GX388 (c) 1984 */
        //TODO 	DRIVER( hyperspt )	/* GX330 (c) 1984 + Centuri */
        //TODO 	DRIVER( hpolym84 )	/* GX330 (c) 1984 */
        //TODO 	DRIVER( sbasketb )	/* GX405 (c) 1984 */
        //TODO 	DRIVER( sbasketo )	/* GX405 (c) 1984 */
        //TODO 	DRIVER( sbasketu )	/* GX405 (c) 1984 */
        //TODO 	DRIVER( mikie )		/* GX469 (c) 1984 */
        //TODO 	DRIVER( mikiej )	/* GX469 (c) 1984 */
        //TODO 	DRIVER( mikiehs )	/* GX469 (c) 1984 */
        //TODO 	DRIVER( roadf )		/* GX461 (c) 1984 */
        //TODO 	DRIVER( roadf2 )	/* GX461 (c) 1984 */
        //TODO 	DRIVER( yiear )		/* GX407 (c) 1985 */
        //TODO 	DRIVER( yiear2 )	/* GX407 (c) 1985 */
        //TODO 	DRIVER( kicker )	/* GX477 (c) 1985 */
        //TODO 	DRIVER( shaolins )	/* GX477 (c) 1985 */
        //TODO 	DRIVER( pingpong )	/* GX555 (c) 1985 */
        //TODO 	DRIVER( gberet )	/* GX577 (c) 1985 */
        //TODO 	DRIVER( rushatck )	/* GX577 (c) 1985 */
        //TODO 	DRIVER( gberetb )	/* bootleg on different hardware */
        //TODO 	DRIVER( mrgoemon )	/* GX621 (c) 1986 (Japan) */
        //TODO 	DRIVER( jailbrek )	/* GX507 (c) 1986 */
        //TODO 	DRIVER( manhatan )	/* GX507 (c) 1986 (Japan) */
        //TODO 	DRIVER( finalizr )	/* GX523 (c) 1985 */
        //TODO 	DRIVER( finalizb )	/* bootleg */
        //TODO 	DRIVER( ironhors )	/* GX560 (c) 1986 */
        //TODO 	DRIVER( dairesya )	/* GX560 (c) 1986 (Japan) */
        //TODO 	DRIVER( farwest )
        //TODO 	DRIVER( jackal )	/* GX631 (c) 1986 (World) */
        //TODO 	DRIVER( topgunr )	/* GX631 (c) 1986 (US) */
        //TODO 	DRIVER( jackalj )	/* GX631 (c) 1986 (Japan) */
        //TODO 	DRIVER( topgunbl )	/* bootleg */
        //TODO 	DRIVER( ddribble )	/* GX690 (c) 1986 */
        //TODO 	DRIVER( contra )	/* GX633 (c) 1987 */
        //TODO 	DRIVER( contrab )	/* bootleg */
        //TODO 	DRIVER( contraj )	/* GX633 (c) 1987 (Japan) */
        //TODO 	DRIVER( contrajb )	/* bootleg */
        //TODO 	DRIVER( gryzor )	/* GX633 (c) 1987 */
        //TODO 	DRIVER( combasc )	/* GX611 (c) 1988 */
        //TODO 	DRIVER( combasct )	/* GX611 (c) 1987 */
        //TODO 	DRIVER( combascj )	/* GX611 (c) 1987 (Japan) */
        //TODO 	DRIVER( bootcamp )	/* GX611 (c) 1987 */
        //TODO 	DRIVER( combascb )	/* bootleg */
        //TODO 	DRIVER( rockrage )	/* GX620 (c) 1986 (World?) */
        //TODO 	DRIVER( rockragj )	/* GX620 (c) 1986 (Japan) */
        //TODO 	DRIVER( mx5000 )	/* GX669 (c) 1987 */
        //TODO 	DRIVER( flkatck )	/* GX669 (c) 1987 (Japan) */
        //TODO 	DRIVER( fastlane )	/* GX752 (c) 1987 */
        //TODO 	DRIVER( tricktrp )	/* GX771 (c) 1987 */
        //TODO 	DRIVER( labyrunr )	/* GX771 (c) 1987 (Japan) */
        //TODO 	DRIVER( thehustl )	/* GX765 (c) 1987 (Japan) */
        //TODO 	DRIVER( thehustj )	/* GX765 (c) 1987 (Japan) */
        //TODO 	DRIVER( rackemup )	/* GX765 (c) 1987 */
        //TODO 	DRIVER( battlnts )	/* GX777 (c) 1987 */
        //TODO 	DRIVER( battlntj )	/* GX777 (c) 1987 (Japan) */
        //TODO 	DRIVER( bladestl )	/* GX797 (c) 1987 */
        //TODO 	DRIVER( bladstle )	/* GX797 (c) 1987 */
        //TODO 	DRIVER( hcastle )	/* GX768 (c) 1988 */
        //TODO 	DRIVER( hcastleo )	/* GX768 (c) 1988 */
        //TODO 	DRIVER( hcastlej )	/* GX768 (c) 1988 (Japan) */
        //TODO 	DRIVER( hcastljo )	/* GX768 (c) 1988 (Japan) */
        //TODO 	DRIVER( ajax )		/* GX770 (c) 1987 */
        //TODO 	DRIVER( typhoon )	/* GX770 (c) 1987 */
        //TODO 	DRIVER( ajaxj )		/* GX770 (c) 1987 (Japan) */
        //TODO 	DRIVER( scontra )	/* GX775 (c) 1988 */
        //TODO 	DRIVER( scontraj )	/* GX775 (c) 1988 (Japan) */
        //TODO 	DRIVER( thunderx )	/* GX873 (c) 1988 */
        //TODO 	DRIVER( thnderxj )	/* GX873 (c) 1988 (Japan) */
        //TODO 	DRIVER( mainevt )	/* GX799 (c) 1988 */
        //TODO 	DRIVER( mainevt2 )	/* GX799 (c) 1988 */
        //TODO 	DRIVER( ringohja )	/* GX799 (c) 1988 (Japan) */
        //TODO 	DRIVER( devstors )	/* GX890 (c) 1988 */
        //TODO 	DRIVER( devstor2 )	/* GX890 (c) 1988 */
        //TODO 	DRIVER( devstor3 )	/* GX890 (c) 1988 */
        //TODO 	DRIVER( garuka )	/* GX890 (c) 1988 (Japan) */
        //TODO 	DRIVER( 88games )	/* GX861 (c) 1988 */
        //TODO 	DRIVER( konami88 )	/* GX861 (c) 1988 */
        //TODO 	DRIVER( hypsptsp )	/* GX861 (c) 1988 (Japan) */
        //TODO 	DRIVER( gbusters )	/* GX878 (c) 1988 */
        //TODO 	DRIVER( crazycop )	/* GX878 (c) 1988 (Japan) */
        //TODO 	DRIVER( crimfght )	/* GX821 (c) 1989 (US) */
        //TODO 	DRIVER( crimfgt2 )	/* GX821 (c) 1989 (World) */
        //TODO 	DRIVER( crimfgtj )	/* GX821 (c) 1989 (Japan) */
        //TODO 	DRIVER( spy )		/* GX857 (c) 1989 (World) */
        //TODO 	DRIVER( spyu )		/* GX857 (c) 1989 (US) */
        //TODO 	DRIVER( bottom9 )	/* GX891 (c) 1989 */
        //TODO 	DRIVER( bottom9n )	/* GX891 (c) 1989 */
        //TODO 	DRIVER( mstadium )	/* GX891 (c) 1989 (Japan) */
        //TODO 	DRIVER( blockhl )	/* GX973 (c) 1989 */
        //TODO 	DRIVER( quarth )	/* GX973 (c) 1989 (Japan) */
        //TODO 	DRIVER( aliens )	/* GX875 (c) 1990 (World) */
        //TODO 	DRIVER( aliens2 )	/* GX875 (c) 1990 (World) */
        //TODO 	DRIVER( aliensu )	/* GX875 (c) 1990 (US) */
        //TODO 	DRIVER( aliensj )	/* GX875 (c) 1990 (Japan) */
        //TODO 	DRIVER( surpratk )	/* GX911 (c) 1990 (Japan) */
        //TODO 	DRIVER( parodius )	/* GX955 (c) 1990 (Japan) */
        //TODO 	DRIVER( rollerg )	/* GX999 (c) 1991 (US) */
        //TODO 	DRIVER( rollergj )	/* GX999 (c) 1991 (Japan) */
        //TODO 	DRIVER( simpsons )	/* GX072 (c) 1991 */
        //TODO 	DRIVER( simpsn2p )	/* GX072 (c) 1991 */
        //TODO 	DRIVER( simps2pj )	/* GX072 (c) 1991 (Japan) */
        //TODO 	DRIVER( esckids )	/* GX975 (c) 1991 (Japan) */
        //TODO 	DRIVER( vendetta )	/* GX081 (c) 1991 (World) */
        //TODO 	DRIVER( vendetao )	/* GX081 (c) 1991 (World) */
        //TODO 	DRIVER( vendet2p )	/* GX081 (c) 1991 (World) */
        //TODO 	DRIVER( vendetas )	/* GX081 (c) 1991 (Asia) */
        //TODO 	DRIVER( vendtaso )	/* GX081 (c) 1991 (Asia) */
        //TODO 	DRIVER( vendettj )	/* GX081 (c) 1991 (Japan) */
        //TODO 	DRIVER( wecleman )	/* GX602 (c) 1986 */
        //TODO 	DRIVER( hotchase )	/* GX763 (c) 1988 */
        //TODO 	DRIVER( chqflag )	/* GX717 (c) 1988 */
        //TODO 	DRIVER( chqflagj )	/* GX717 (c) 1988 (Japan) */
        //TODO 	DRIVER( ultraman )	/* GX910 (c) 1991 Banpresto/Bandai */
        //TODO 	DRIVER( hexion )	/* GX122 (c) 1992 */
        //TODO 
        //TODO 	/* Konami "Nemesis hardware" games */
        //TODO 	DRIVER( nemesis )	/* GX456 (c) 1985 */
        //TODO 	DRIVER( nemesuk )	/* GX456 (c) 1985 */
        //TODO 	DRIVER( konamigt )	/* GX561 (c) 1985 */
        //TODO 	DRIVER( salamand )	/* GX587 (c) 1986 */
        //TODO 	DRIVER( salamanj )	/* GX587 (c) 1986 */
        //TODO 	DRIVER( lifefrce )	/* GX587 (c) 1986 (US) */
        //TODO 	DRIVER( lifefrcj )	/* GX587 (c) 1986 (Japan) */
        //TODO 	DRIVER( blkpnthr )	/* GX604 (c) 1987 (Japan) */
        //TODO 	DRIVER( citybomb )	/* GX787 (c) 1987 (World) */
        //TODO 	DRIVER( citybmrj )	/* GX787 (c) 1987 (Japan) */
        //TODO 	DRIVER( kittenk )	/* GX712 (c) 1988 */
        //TODO 	DRIVER( nyanpani )	/* GX712 (c) 1988 (Japan) */
        //TODO 
        //TODO 	/* GX400 BIOS based games */
        //TODO 	DRIVER( rf2 )		/* GX561 (c) 1985 */
        //TODO 	DRIVER( twinbee )	/* GX412 (c) 1985 */
        //TODO 	DRIVER( gradius )	/* GX456 (c) 1985 */
        //TODO 	DRIVER( gwarrior )	/* GX578 (c) 1985 */
        //TODO 
        //TODO 	/* Konami "Twin 16" games */
        //TODO 	DRIVER( devilw )	/* GX687 (c) 1987 */
        //TODO 	DRIVER( darkadv )	/* GX687 (c) 1987 */
        //TODO 	DRIVER( majuu )		/* GX687 (c) 1987 (Japan) */
        //TODO 	DRIVER( vulcan )	/* GX785 (c) 1988 */
        //TODO 	DRIVER( gradius2 )	/* GX785 (c) 1988 (Japan) */
        //TODO 	DRIVER( grdius2a )	/* GX785 (c) 1988 (Japan) */
        //TODO 	DRIVER( grdius2b )	/* GX785 (c) 1988 (Japan) */
        //TODO 	DRIVER( cuebrick )	/* GX903 (c) 1989 */
        //TODO 	DRIVER( fround )	/* GX870 (c) 1988 */
        //TODO 	DRIVER( froundl )	/* GX870 (c) 1988 */
        //TODO 	DRIVER( hpuncher )	/* GX870 (c) 1988 (Japan) */
        //TODO 	DRIVER( miaj )		/* GX808 (c) 1989 (Japan) */
        //TODO 
        //TODO 	/* (some) Konami 68000 games */
        //TODO 	DRIVER( mia )		/* GX808 (c) 1989 */
        //TODO 	DRIVER( mia2 )		/* GX808 (c) 1989 */
        //TODO 	DRIVER( tmnt )		/* GX963 (c) 1989 (World) */
        //TODO 	DRIVER( tmntu )		/* GX963 (c) 1989 (US) */
        //TODO 	DRIVER( tmht )		/* GX963 (c) 1989 (UK) */
        //TODO 	DRIVER( tmntj )		/* GX963 (c) 1990 (Japan) */
        //TODO 	DRIVER( tmht2p )	/* GX963 (c) 1989 (UK) */
        //TODO 	DRIVER( tmnt2pj )	/* GX963 (c) 1990 (Japan) */
        //TODO 	DRIVER( tmnt2po )	/* GX963 (c) 1989 (Oceania) */
        //TODO 	DRIVER( punkshot )	/* GX907 (c) 1990 (US) */
        //TODO 	DRIVER( punksht2 )	/* GX907 (c) 1990 (US) */
        //TODO 	DRIVER( punkshtj )	/* GX907 (c) 1990 (Japan) */
        //TODO 	DRIVER( lgtnfght )	/* GX939 (c) 1990 (US) */
        //TODO 	DRIVER( trigon )	/* GX939 (c) 1990 (Japan) */
        //TODO 	DRIVER( blswhstl )	/* GX060 (c) 1991 */
        //TODO 	DRIVER( detatwin )	/* GX060 (c) 1991 (Japan) */
        //TODO 	DRIVER( glfgreat )	/* GX061 (c) 1991 */
        //TODO 	DRIVER( glfgretj )	/* GX061 (c) 1991 (Japan) */
        //TODO 	DRIVER( tmnt2 )		/* GX063 (c) 1991 (US) */
        //TODO 	DRIVER( tmnt22p )	/* GX063 (c) 1991 (US) */
        //TODO 	DRIVER( tmnt2a )	/* GX063 (c) 1991 (Asia) */
        //TODO 	DRIVER( ssriders )	/* GX064 (c) 1991 (World) */
        //TODO 	DRIVER( ssrdrebd )	/* GX064 (c) 1991 (World) */
        //TODO 	DRIVER( ssrdrebc )	/* GX064 (c) 1991 (World) */
        //TODO 	DRIVER( ssrdruda )	/* GX064 (c) 1991 (US) */
        //TODO 	DRIVER( ssrdruac )	/* GX064 (c) 1991 (US) */
        //TODO 	DRIVER( ssrdrubc )	/* GX064 (c) 1991 (US) */
        //TODO 	DRIVER( ssrdrabd )	/* GX064 (c) 1991 (Asia) */
        //TODO 	DRIVER( ssrdrjbd )	/* GX064 (c) 1991 (Japan) */
        //TODO 	DRIVER( xmen )		/* GX065 (c) 1992 (US) */
        //TODO 	DRIVER( xmen2p )	/* GX065 (c) 1992 (World) */
        //TODO 	DRIVER( xmen2pj )	/* GX065 (c) 1992 (Japan) */
        //TODO TESTDRIVER( xmen6p )	/* GX065 (c) 1992 */
        //TODO 	DRIVER( xexex )		/* GX067 (c) 1991 (World) */
        //TODO 	DRIVER( xexexj )	/* GX067 (c) 1991 (Japan) */
        //TODO 	DRIVER( asterix )	/* GX068 (c) 1992 (World) */
        //TODO 	DRIVER( astrxeac )	/* GX068 (c) 1992 (World) */
        //TODO 	DRIVER( astrxeaa )	/* GX068 (c) 1992 (World) */
        //TODO 	DRIVER( gijoe )		/* GX069 (c) 1991 (World) */
        //TODO 	DRIVER( gijoeu )	/* GX069 (c) 1991 (US) */
        //TODO 	DRIVER( thndrx2 )	/* GX073 (c) 1991 (Japan) */
        //TODO 	DRIVER( thndrx2a )	/* GX073 (c) 1991 (Asia) */
        //TODO 	DRIVER( prmrsocr )	/* GX101 (c) 1993 (Japan) */
        //TODO 	DRIVER( qgakumon )	/* GX248 (c) 1993 (Japan) */
        //TODO 	DRIVER( moo )		/* GX151 (c) 1992 (World) */
        //TODO 	DRIVER( mooua )		/* GX151 (c) 1992 (US) */
        //TODO 	DRIVER( bucky )		/* GX173 (c) 1992 (World) */
        //TODO 	DRIVER( buckyua )	/* GX173 (c) 1992 (US) */
        //TODO 	DRIVER( gaiapols )	/* GX123 (c) 1993 (Japan) */
        //TODO 	DRIVER( mystwarr )	/* GX128 (c) 1993 (World) */
        //TODO 	DRIVER( mystwaru )	/* GX128 (c) 1993 (US) */
        //TODO 	DRIVER( viostorm )	/* GX168 (c) 1993 (US) */
        //TODO 	DRIVER( viostrmj )	/* GX168 (c) 1993 (Japan) */
        //TODO 	DRIVER( viostrma )	/* GX168 (c) 1993 (Asia) */
        //TODO 	DRIVER( dadandrn )	/* GX170 (c) 1993 (Japan) */
        //TODO 	DRIVER( metamrph )	/* GX224 (c) 1993 (US) */
        //TODO 	DRIVER( mtlchmpj )	/* GX234 (c) 1993 (Japan) */
        //TODO 	DRIVER( rungun )	/* GX247 (c) 1993 (World) */
        //TODO 	DRIVER( rungunu )	/* GX247 (c) 1993 (US) */
        //TODO 	DRIVER( slmdunkj )	/* GX247 (c) 1993 (Japan) */
        //TODO 	DRIVER( dbz2 )		/* (c) 1994 Banpresto */
        //TODO 
        //TODO 	/* Konami dual 68000 games */
        //TODO 	DRIVER( overdriv )	/* GX789 (c) 1990 */
        //TODO 	DRIVER( gradius3 )	/* GX945 (c) 1989 (Japan) */
        //TODO 	DRIVER( grdius3a )	/* GX945 (c) 1989 (Asia) */
        //TODO 	DRIVER( grdius3e )	/* GX945 (c) 1989 (World?) */
        //TODO 
        //TODO 	/* Konami System GX games */
        //TODO 	/* 1994.03 Golfing Greats 2 (GX218) */
        //TODO TESTDRIVER( racinfrc )	/* GX250 */
        //TODO 	DRIVER( le2 )		/* GX312 (c) 1994 */
        //TODO 	DRIVER( puzldama )	/* GX315 (c) 1994 (Japan) */
        //TODO 	DRIVER( gokuparo )	/* GX321 (c) 1994 (Japan) */
        //TODO 	DRIVER( fantjour )	/* GX321 */
        //TODO TESTDRIVER( dragoonj )	/* GX417 (c) 1995 (Japan) */
        //TODO 	DRIVER( tbyahhoo )	/* GX424 (c) 1995 (Japan) */
        //TODO TESTDRIVER( soccerss )	/* GX427 */
        //TODO TESTDRIVER( tkmmpzdm )	/* GX515 (c) 1995 (Japan) */
        //TODO TESTDRIVER( salmndr2 )	/* GX521 (c) 1996 (Japan) */
        //TODO 	DRIVER( sexyparo )	/* GX533 (c) 1996 (Japan) */
        //TODO 	DRIVER( daiskiss )	/* GX535 (c) 1996 (Japan) */
        //TODO 	/* 1997.11 Rushing Heroes (GX605) */
        //TODO 	DRIVER( tokkae )	/* GX615 (c) 1996 (Japan) */
        //TODO TESTDRIVER( vsnetscr )	/* GX627 */
        //TODO TESTDRIVER( winspike )	/* GX705 */
        //TODO 
        //TODO TESTDRIVER( rungun2 )	/* GX505 */
        //TODO 
        //TODO 	/* Exidy games */
        //TODO 	DRIVER( carpolo )	/* (c) 1977 */
        //TODO 	DRIVER( sidetrac )	/* (c) 1979 */
        //TODO 	DRIVER( targ )		/* (c) 1980 */
        //TODO 	DRIVER( spectar )	/* (c) 1980 */
        //TODO 	DRIVER( spectar1 )	/* (c) 1980 */
        //TODO 	DRIVER( venture )	/* (c) 1981 */
        //TODO 	DRIVER( venture2 )	/* (c) 1981 */
        //TODO 	DRIVER( venture4 )	/* (c) 1981 */
        //TODO 	DRIVER( mtrap )		/* (c) 1981 */
        //TODO 	DRIVER( mtrap3 )	/* (c) 1981 */
        //TODO 	DRIVER( mtrap4 )	/* (c) 1981 */
        //TODO 	DRIVER( pepper2 )	/* (c) 1982 */
        //TODO 	DRIVER( hardhat )	/* (c) 1982 */
        //TODO 	DRIVER( fax )		/* (c) 1983 */
        //TODO 	DRIVER( circus )	/* no copyright notice [1977?] */
        //TODO 	DRIVER( robotbwl )	/* no copyright notice */
        //TODO 	DRIVER( crash )		/* Exidy [1979?] */
        //TODO 	DRIVER( ripcord )	/* Exidy [1977?] */
        //TODO 	DRIVER( starfire )	/* Exidy [1979?] */
        //TODO 	DRIVER( starfira )	/* Exidy [1979?] */
        //TODO 	DRIVER( fireone )	/* (c) 1979 Exidy */
        //TODO 	DRIVER( victory )	/* (c) 1982 */
        //TODO 	DRIVER( victorba )	/* (c) 1982 */
        //TODO 
        //TODO 	/* Exidy 440 games */
        //TODO 	DRIVER( crossbow )	/* (c) 1983 */
        //TODO 	DRIVER( cheyenne )	/* (c) 1984 */
        //TODO 	DRIVER( combat )	/* (c) 1985 */
        //TODO 	DRIVER( catch22 )	/* (c) 1985 */
        //TODO 	DRIVER( cracksht )	/* (c) 1985 */
        //TODO 	DRIVER( claypign )	/* (c) 1986 */
        //TODO 	DRIVER( chiller )	/* (c) 1986 */
        //TODO 	DRIVER( topsecex )	/* (c) 1986 */
        //TODO 	DRIVER( hitnmiss )	/* (c) 1987 */
        //TODO 	DRIVER( hitnmis2 )	/* (c) 1987 */
        //TODO 	DRIVER( whodunit )	/* (c) 1988 */
        //TODO 	DRIVER( showdown )	/* (c) 1988 */
        //TODO 
        //TODO 	/* Atari b/w games */
        //TODO 	/* Tank 8 */  		/* ??????			1976/04 [6800] */
        //TODO 	DRIVER( copsnrob )	/* 005625			1976/07 [6502] */
        //TODO 	DRIVER( flyball )	/* 005629			1976/07 [6502] */
        //TODO 	DRIVER( sprint2 )	/* 005922			1976/11 [6502] */
        //TODO 	DRIVER( nitedrvr )	/* 006321			1976/10 [6502] */
        //TODO 	DRIVER( dominos )	/* 007305			1977/01 [6502] */
        //TODO 	DRIVER( triplhnt )	/* 008422-008791	1977/04 [6800] */
        //TODO 	/* Sprint 8 */		/* ??????			1977/05 [6800] */
        //TODO 	DRIVER( dragrace )	/* 008505-008521	1977/06 [6800] */
        //TODO 	DRIVER( poolshrk )	/* 006281			1977/06 [6800] */
        //TODO 	DRIVER( starshp1 )	/* 007513-007531	1977/07 [6502] */
        //TODO 	DRIVER( starshpp )	/* 007513-007531	1977/07 [6502] */
        //TODO 	DRIVER( superbug )	/* 009115-009467	1977/09 [6800] */
        //TODO 	DRIVER( canyon )	/* 009493-009504	1977/10 [6502] */
        //TODO 	DRIVER( canbprot )	/* 009493-009504	1977/10 [6502] */
        //TODO 	DRIVER( destroyr )	/* 030131-030136	1977/10 [6800] */
        //TODO 	/* Sprint 4 */		/* 008716			1977/12 [6502] */
        //TODO 	DRIVER( sprint1 )	/* 006443			1978/01 [6502] */
        //TODO 	DRIVER( ultratnk )	/* 009801			1978/02 [6502] */
        //TODO 	DRIVER( skyraid )	/* 009709			1978/03 [6502] */
        //TODO 	/* Tourn. Table */	/* 030170			1978/03 [6507] */
        //TODO 	DRIVER( avalnche )	/* 030574			1978/04 [6502] */
        //TODO 	DRIVER( firetrk )	/* 030926			1978/06 [6808] */
        //TODO 	DRIVER( skydiver )	/* 009787			1978/06 [6800] */
        //TODO 	/* Smokey Joe */	/* 030926			1978/07 [6502] */
        //TODO 	DRIVER( sbrkout )	/* 033442-033455	1978/09 [6502] */
        //TODO 	DRIVER( atarifb )	/* 033xxx			1978/10 [6502] */
        //TODO 	DRIVER( atarifb1 )	/* 033xxx			1978/10 [6502] */
        //TODO 	DRIVER( orbit )		/* 033689-033702	1978/11 [6800] */
        //TODO 	DRIVER( videopin )	/* 034253-034267	1979/02 [6502] */
        //TODO 	DRIVER( atarifb4 )	/* 034754			1979/04 [6502] */
        //TODO 	DRIVER( subs )		/* 033714			1979/05 [6502] */
        //TODO 	DRIVER( bsktball )	/* 034756-034766	1979/05 [6502] */
        //TODO 	DRIVER( abaseb )	/* 034711-034738	1979/06 [6502] */
        //TODO 	DRIVER( abaseb2 )	/* 034711-034738	1979/06 [6502] */
        //TODO 	DRIVER( montecar )	/* 035763-035780	1980/04 [6502] */
        //TODO 	DRIVER( soccer )	/* 035222-035260	1980/04 [6502] */
        //TODO 
        //TODO 	/* Atari "Missile Command hardware" games */
        //TODO 	DRIVER( missile )	/* 035820-035825	(c) 1980 */
        //TODO 	DRIVER( missile2 )	/* 035820-035825	(c) 1980 */
        //TODO 	DRIVER( suprmatk )	/* 					(c) 1980 + (c) 1981 Gencomp */
        //TODO 
        //TODO 	/* Atari vector games */
        //TODO 	DRIVER( llander )	/* 0345xx			no copyright notice */
        //TODO 	DRIVER( llander1 )	/* 0345xx			no copyright notice */
        //TODO 	DRIVER( asteroid )	/* 035127-035145	(c) 1979 */
        //TODO 	DRIVER( asteroi1 )	/* 035127-035145	no copyright notice */
        //TODO 	DRIVER( asteroib )	/* (bootleg) */
        //TODO 	DRIVER( astdelux )	/* 0351xx			(c) 1980 */
        //TODO 	DRIVER( astdelu1 )	/* 0351xx			(c) 1980 */
        //TODO 	DRIVER( bzone )		/* 0364xx			(c) 1980 */
        //TODO 	DRIVER( bzone2 )	/* 0364xx			(c) 1980 */
        //TODO 	DRIVER( bzonec )	/* 0364xx			(c) 1980 */
        //TODO 	DRIVER( bradley )	/*     ??			(c) 1980 */
        //TODO 	DRIVER( redbaron )	/* 036995-037007	(c) 1980 */
        //TODO 	DRIVER( tempest )	/* 136002			(c) 1980 */
        //TODO 	DRIVER( tempest1 )	/* 136002			(c) 1980 */
        //TODO 	DRIVER( tempest2 )	/* 136002			(c) 1980 */
        //TODO 	DRIVER( temptube )	/* (hack) */
        //TODO 	DRIVER( spacduel )	/* 136006			(c) 1980 */
        //TODO 	DRIVER( gravitar )	/* 136010			(c) 1982 */
        //TODO 	DRIVER( gravitr2 )	/* 136010			(c) 1982 */
        //TODO 	DRIVER( gravp )		/* (proto)			(c) 1982 */
        //TODO 	DRIVER( lunarbat )	/* (proto)			(c) 1982 */
        //TODO 	DRIVER( quantum )	/* 136016			(c) 1982 */	/* made by Gencomp */
        //TODO 	DRIVER( quantum1 )	/* 136016			(c) 1982 */	/* made by Gencomp */
        //TODO 	DRIVER( quantump )	/* 136016			(c) 1982 */	/* made by Gencomp */
        //TODO 	DRIVER( bwidow )	/* 136017			(c) 1982 */
        //TODO 	DRIVER( starwars )	/* 136021			(c) 1983 */
        //TODO 	DRIVER( starwar1 )	/* 136021			(c) 1983 */
        //TODO 	DRIVER( mhavoc )	/* 136025			(c) 1983 */
        //TODO 	DRIVER( mhavoc2 )	/* 136025			(c) 1983 */
        //TODO 	DRIVER( mhavocp )	/* 136025			(c) 1983 */
        //TODO 	DRIVER( mhavocrv )	/* (hack) */
        //TODO 	DRIVER( alphaone )	/* (proto)          (c) 1983 */
        //TODO 	DRIVER( alphaona )	/* (proto)          (c) 1983 */
        //TODO 	DRIVER( esb )		/* 136031			(c) 1985 */
        //TODO 
        //TODO 	/* Atari "Centipede hardware" games */
        //TODO 	DRIVER( warlords )	/* 037153-037159	(c) 1980 */
        //TODO 	DRIVER( centiped )	/* 136001			(c) 1980 */
        //TODO 	DRIVER( centipd2 )	/* 136001			(c) 1980 */
        //TODO 	DRIVER( centtime )	/* 136001			(c) 1980 */
        //TODO 	DRIVER( centipdb )	/* (bootleg) */
        //TODO 	DRIVER( centipb2 )	/* (bootleg) */
        //TODO 	DRIVER( magworm )	/* (bootleg) */
        //TODO 	DRIVER( milliped )	/* 136013			(c) 1982 */
        //TODO 	DRIVER( qwakprot )	/* (proto)			(c) 1982 */
        //TODO 
        //TODO 	/* misc Atari games */
        //TODO 	DRIVER( tunhunt )	/* 136000			(c) 1981 */
        //TODO 	DRIVER( liberatr )	/* 136012			(c) 1982 */
        //TODO TESTDRIVER( liberat2 )	/* 136012			(c) 1982 */
        //TODO 	DRIVER( foodf )		/* 136020			(c) 1982 */	/* made by Gencomp */
        //TODO 	DRIVER( ccastles )	/* 136022			(c) 1983 */
        //TODO 	DRIVER( ccastle3 )	/* 136022			(c) 1983 */
        //TODO 	DRIVER( ccastle2 )	/* 136022			(c) 1983 */
        //TODO 	DRIVER( cloak )		/* 136023			(c) 1983 */
        //TODO 	DRIVER( cloud9 )	/* (proto)			(c) 1983 */
        //TODO 	DRIVER( jedi )		/* 136030			(c) 1984 */
        //TODO 
        //TODO 	/* Atari System 1 games */
        //TODO 	DRIVER( peterpak )	/* 136028			(c) 1984 */
        //TODO 	DRIVER( marble )	/* 136033			(c) 1984 */
        //TODO 	DRIVER( marble2 )	/* 136033			(c) 1984 */
        //TODO 	DRIVER( marble3 )	/* 136033			(c) 1984 */
        //TODO 	DRIVER( marble4 )	/* 136033			(c) 1984 */
        //TODO 	DRIVER( indytemp )	/* 136036			(c) 1985 */
        //TODO 	DRIVER( indytem2 )	/* 136036			(c) 1985 */
        //TODO 	DRIVER( indytem3 )	/* 136036			(c) 1985 */
        //TODO 	DRIVER( indytem4 )	/* 136036			(c) 1985 */
        //TODO 	DRIVER( indytemd )	/* 136036           (c) 1985 */
        //TODO 	DRIVER( roadrunn )	/* 136040			(c) 1985 */
        //TODO 	DRIVER( roadblst )	/* 136048			(c) 1986, 1987 */
        //TODO 	DRIVER( roadbls2 )	/* 136048			(c) 1986, 1987 */
        //TODO 
        //TODO 	/* Atari System 2 games */
        //TODO 	DRIVER( paperboy )	/* 136034			(c) 1984 */
        //TODO 	DRIVER( ssprint )	/* 136042			(c) 1986 */
        //TODO 	DRIVER( csprint )	/* 136045			(c) 1986 */
        //TODO 	DRIVER( 720 )		/* 136047			(c) 1986 */
        //TODO 	DRIVER( 720b )		/* 136047			(c) 1986 */
        //TODO 	DRIVER( apb )		/* 136051			(c) 1987 */
        //TODO 	DRIVER( apb2 )		/* 136051			(c) 1987 */
        //TODO 
        //TODO 	/* Atari polygon games */
        //TODO 	DRIVER( irobot )	/* 136029			(c) 1983 */
        //TODO 	DRIVER( harddriv )	/* 136052			(c) 1988 */
        //TODO 	DRIVER( harddrvc )	/* 136068			(c) 1990 */
        //TODO 	DRIVER( stunrun )	/* 136070			(c) 1989 */
        //TODO 	DRIVER( stunrnp )	/* (proto)			(c) 1989 */
        //TODO 	DRIVER( racedriv )	/* 136077			(c) 1990 */
        //TODO 	DRIVER( racedrv3 )	/* 136077			(c) 1990 */
        //TODO 	DRIVER( racedrvc )	/* 136077			(c) 1990 */
        //TODO 	DRIVER( steeltal )	/* 136087			(c) 1990 */
        //TODO 	DRIVER( steeltap )	/* 136087			(c) 1990 */
        //TODO 	DRIVER( hdrivair )	/* (proto) */
        //TODO 	DRIVER( hdrivaip )	/* (proto) */
        //TODO 
        //TODO 	/* later Atari games */
        //TODO 	DRIVER( gauntlet )	/* 136037			(c) 1985 */
        //TODO 	DRIVER( gauntir1 )	/* 136037			(c) 1985 */
        //TODO 	DRIVER( gauntir2 )	/* 136037			(c) 1985 */
        //TODO 	DRIVER( gaunt2p )	/*     ??			(c) 1985 */
        //TODO 	DRIVER( gaunt2 )	/* 136043			(c) 1986 */
        //TODO 	DRIVER( vindctr2 )	/*     ??			(c) 1988 */
        //TODO 	DRIVER( xybots )	/* 136054			(c) 1987 */
        //TODO 	DRIVER( blstroid )	/* 136057			(c) 1987 */
        //TODO 	DRIVER( blstroi2 )	/* 136057			(c) 1987 */
        //TODO 	DRIVER( blsthead )	/* (proto)			(c) 1987 */
        //TODO 	DRIVER( vindictr )	/* 136059			(c) 1988 */
        //TODO 	DRIVER( vindicta )	/* 136059			(c) 1988 */
        //TODO 	DRIVER( toobin )	/* 136061			(c) 1988 */
        //TODO 	DRIVER( toobin2 )	/* 136061			(c) 1988 */
        //TODO 	DRIVER( toobinp )	/* (proto)			(c) 1988 */
        //TODO 	DRIVER( cyberbal )	/* 136064			(c) 1989 */
        //TODO 	DRIVER( cyberba2 )	/* 136064			(c) 1989 */
        //TODO 	DRIVER( atetcktl )	/* 136066			(c) 1989 */
        //TODO 	DRIVER( atetckt2 )	/* 136066			(c) 1989 */
        //TODO 	DRIVER( atetris )	/* 136066			(c) 1988 */
        //TODO 	DRIVER( atetrisa )	/* 136066			(c) 1988 */
        //TODO 	DRIVER( atetrisb )	/* (bootleg) */
        //TODO 	DRIVER( eprom )		/* 136069			(c) 1989 */
        //TODO 	DRIVER( eprom2 )	/* 136069			(c) 1989 */
        //TODO 	DRIVER( skullxbo )	/* 136072			(c) 1989 */
        //TODO 	DRIVER( skullxb2 )	/* 136072			(c) 1989 */
        //TODO 	DRIVER( cyberbt )	/* 136073			(c) 1989 */
        //TODO 	DRIVER( badlands )	/* 136074			(c) 1989 */
        //TODO 	DRIVER( klax )		/* 136075			(c) 1989 */
        //TODO 	DRIVER( klax2 )		/* 136075			(c) 1989 */
        //TODO 	DRIVER( klax3 )		/* 136075			(c) 1989 */
        //TODO 	DRIVER( klaxj )		/* 136075			(c) 1989 (Japan) */
        //TODO 	DRIVER( klaxd )		/* 136075			(c) 1989 (Germany) */
        //TODO 	DRIVER( klaxp1 )	/* prototype */
        //TODO 	DRIVER( klaxp2 )	/* prototype */
        //TODO 	DRIVER( thunderj )	/* 136076			(c) 1990 */
        //TODO 	DRIVER( cyberb2p )	/*     ??			(c) 1989 */
        //TODO 	DRIVER( hydra )		/* 136079			(c) 1990 */
        //TODO 	DRIVER( hydrap )	/* (proto)			(c) 1990 */
        //TODO 	DRIVER( pitfight )	/* 136081			(c) 1990 */
        //TODO 	DRIVER( pitfigh3 )	/* 136081			(c) 1990 */
        //TODO 	DRIVER( pitfighb )	/* bootleg */
        //TODO 	DRIVER( rampart )	/* 136082			(c) 1990 */
        //TODO 	DRIVER( ramprt2p )	/* 136082			(c) 1990 */
        //TODO 	DRIVER( rampartj )	/* 136082			(c) 1990 (Japan) */
        //TODO 	DRIVER( shuuz )		/* 136083			(c) 1990 */
        //TODO 	DRIVER( shuuz2 )	/* 136083			(c) 1990 */
        //TODO 	DRIVER( batman )	/* 136085			(c) 1991 */
        //TODO TESTDRIVER( roadriot )	/* 136089			(c) 1991 */
        //TODO 	DRIVER( offtwall )	/* 136090			(c) 1991 */
        //TODO 	DRIVER( offtwalc )	/* 136090			(c) 1991 */
        //TODO TESTDRIVER( guardian )	/* 136092			(c) 1992 */
        //TODO 	DRIVER( relief )	/* 136093			(c) 1992 */
        //TODO 	DRIVER( relief2 )	/* 136093			(c) 1992 */
        //TODO 	DRIVER( arcadecl )	/* (proto)			(c) 1992 */
        //TODO 	DRIVER( sparkz )	/* (proto)			(c) 1992 */
        //TODO TESTDRIVER( motofren )	/* 136094			(c) 1992 */
        //TODO TESTDRIVER( spclords )	/* 136095			(c) 1992 */
        //TODO TESTDRIVER( spclorda )	/* 136095			(c) 1992 */
        //TODO 	DRIVER( rrreveng )	/*     ??			(c) 1993 */
        //TODO 	DRIVER( beathead )	/* (proto)			(c) 1993 */
        //TODO TESTDRIVER( tmek )		/* 136100			(c) 1994 */
        //TODO TESTDRIVER( tmekprot )	/* 136100			(c) 1994 */
        //TODO 	DRIVER( primrage )	/* 136102			(c) 1994 */
        //TODO 	DRIVER( primrag2 )	/* 136102			(c) 1994 */
        //TODO 	DRIVER( area51 )	/* 136105			(c) 1995 */
        //TODO 	DRIVER( maxforce )	/*     ??			(c) 1996 */
        //TODO 	DRIVER( vcircle )	/*     ??			(c) 1996 */
        //TODO 
        //TODO 	/* SNK / Rock-ola games */
        //TODO 	DRIVER( sasuke )	/* [1980] Shin Nihon Kikaku (SNK) */
        //TODO 	DRIVER( satansat )	/* (c) 1981 SNK */
        //TODO 	DRIVER( zarzon )	/* (c) 1981 Taito, gameplay says SNK */
        //TODO 	DRIVER( vanguard )	/* (c) 1981 SNK */
        //TODO 	DRIVER( vangrdce )	/* (c) 1981 SNK + Centuri */
        //TODO 	DRIVER( fantasy )	/* (c) 1981 Rock-ola */
        //TODO 	DRIVER( fantasyj )	/* (c) 1981 SNK */
        //TODO 	DRIVER( pballoon )	/* (c) 1982 SNK */
        //TODO 	DRIVER( nibbler )	/* (c) 1982 Rock-ola */
        //TODO 	DRIVER( nibblera )	/* (c) 1982 Rock-ola */
        //TODO 
        //TODO 	/* later SNK games, each game can be identified by PCB code and ROM
        //TODO 	code, the ROM code is the same between versions, and usually based
        //TODO 	upon the Japanese title. */
        //TODO 	DRIVER( lasso )		/*       'WM' (c) 1982 */
        //TODO 	DRIVER( chameleo )	/* (c) 1983 Jaleco */
        //TODO 	DRIVER( wwjgtin )	/* (c) 1984 Jaleco / Casio */
        //TODO 	DRIVER( pinbo )		/* (c) 1984 Jaleco */
        //TODO 	DRIVER( pinbos )	/* (c) 1985 Strike */
        //TODO 	DRIVER( joyfulr )	/* A2001      (c) 1983 */
        //TODO 	DRIVER( mnchmobl )	/* A2001      (c) 1983 + Centuri license */
        //TODO 	DRIVER( marvins )	/* A2003      (c) 1983 */
        //TODO 	DRIVER( madcrash )	/* A2005      (c) 1984 */
        //TODO 	DRIVER( vangrd2 )	/*            (c) 1984 */
        //TODO 	DRIVER( sgladiat )	/* A3006      (c) 1984 */
        //TODO 	DRIVER( hal21 )		/*            (c) 1985 */
        //TODO 	DRIVER( hal21j )	/*            (c) 1985 (Japan) */
        //TODO 	DRIVER( aso )		/*            (c) 1985 */
        //TODO 	DRIVER( tnk3 )		/* A5001      (c) 1985 */
        //TODO 	DRIVER( tnk3j )		/* A5001      (c) 1985 */
        //TODO 	DRIVER( athena )	/*       'UP' (c) 1986 */
        //TODO 	DRIVER( fitegolf )	/*       'GU' (c) 1988 */
        //TODO 	DRIVER( fitegol2 )	/*       'GU' (c) 1988 */
        //TODO 	DRIVER( ikari )		/* A5004 'IW' (c) 1986 */
        //TODO 	DRIVER( ikarijp )	/* A5004 'IW' (c) 1986 (Japan) */
        //TODO 	DRIVER( ikarijpb )	/* bootleg */
        //TODO 	DRIVER( victroad )	/*            (c) 1986 */
        //TODO 	DRIVER( dogosoke )	/*            (c) 1986 */
        //TODO 	DRIVER( gwar )		/* A7003 'GV' (c) 1987 */
        //TODO 	DRIVER( gwarj )		/* A7003 'GV' (c) 1987 (Japan) */
        //TODO 	DRIVER( gwara )		/* A7003 'GV' (c) 1987 */
        //TODO 	DRIVER( gwarb )		/* bootleg */
        //TODO 	DRIVER( bermudat )	/* A6003 'WW' (c) 1987 */
        //TODO 	DRIVER( bermudao )	/* A6003 'WW' (c) 1987 */
        //TODO 	DRIVER( bermudaa )	/* A6003 'WW' (c) 1987 */
        //TODO 	DRIVER( worldwar )	/* A6003 'WW' (c) 1987 */
        //TODO 	DRIVER( psychos )	/*       'PS' (c) 1987 */
        //TODO 	DRIVER( psychosj )	/*       'PS' (c) 1987 (Japan) */
        //TODO 	DRIVER( chopper )	/* A7003 'KK' (c) 1988 */
        //TODO 	DRIVER( legofair )	/* A7003 'KK' (c) 1988 */
        //TODO 	DRIVER( ftsoccer )	/*            (c) 1988 */
        //TODO 	DRIVER( tdfever )	/* A6006 'TD' (c) 1987 */
        //TODO 	DRIVER( tdfeverj )	/* A6006 'TD' (c) 1987 */
        //TODO 	DRIVER( ikari3 )	/* A7007 'IK3'(c) 1989 */
        //TODO 	DRIVER( pow )		/* A7008 'DG' (c) 1988 */
        //TODO 	DRIVER( powj )		/* A7008 'DG' (c) 1988 */
        //TODO 	DRIVER( searchar )	/* A8007 'BH' (c) 1989 */
        //TODO 	DRIVER( sercharu )	/* A8007 'BH' (c) 1989 */
        //TODO 	DRIVER( streetsm )	/* A8007 'S2' (c) 1989 */
        //TODO 	DRIVER( streets1 )	/* A7008 'S2' (c) 1989 */
        //TODO 	DRIVER( streetsw )	/*            (c) 1989 */
        //TODO 	DRIVER( streetsj )	/* A8007 'S2' (c) 1989 */
        //TODO 	DRIVER( prehisle )	/* A8003 'GT' (c) 1989 */
        //TODO 	DRIVER( prehislu )	/* A8003 'GT' (c) 1989 */
        //TODO 	DRIVER( gensitou )	/* A8003 'GT' (c) 1989 */
        //TODO 	DRIVER( mechatt )	/* A8002 'MA' (c) 1989 */
        //TODO 	DRIVER( bbusters )	/* A9003 'BB' (c) 1989 */
        //TODO 
        //TODO 	/* Alpha Denshi games */
        //TODO 	DRIVER( champbas )	/* (c) 1983 Sega */
        //TODO 	DRIVER( champbbj )	/* (c) 1983 Alpha Denshi Co. */
        //TODO TESTDRIVER( champbb2 )	/* (c) 1983 Sega */
        //TODO 	DRIVER( exctsccr )	/* (c) 1983 Alpha Denshi Co. */
        //TODO 	DRIVER( exctscca )	/* (c) 1983 Alpha Denshi Co. */
        //TODO 	DRIVER( exctsccb )	/* bootleg */
        //TODO 	DRIVER( exctscc2 )	/* (c) 1984 Alpha Denshi Co. */
        //TODO 	DRIVER( equites )	/* (c) 1984 Alpha Denshi Co. */
        //TODO 	DRIVER( equitess )	/* (c) 1984 Alpha Denshi Co./Sega */
        //TODO 	DRIVER( bullfgtr )	/* (c) 1984 Alpha Denshi Co./Sega */
        //TODO 	DRIVER( kouyakyu )	/* (c) 1985 Alpha Denshi Co. */
        //TODO 	DRIVER( splndrbt )	/* (c) 1985 Alpha Denshi Co. */
        //TODO 	DRIVER( hvoltage )	/* (c) 1985 Alpha Denshi Co. */
        //TODO 
        //TODO 	/* SNK / Alpha 68K games */
        //TODO 	DRIVER( sstingry )	/* (c) 1986 Alpha Denshi Co. */
        //TODO 	DRIVER( kyros )		/* (c) 1987 World Games */
        //TODO 	DRIVER( paddlema )	/* Alpha-68K96I  'PM' (c) 1988 SNK */
        //TODO 	DRIVER( timesold )	/* Alpha-68K96II 'BT' (c) 1987 SNK / Romstar */
        //TODO 	DRIVER( timesol1 )  /* Alpha-68K96II 'BT' (c) 1987 */
        //TODO 	DRIVER( btlfield )  /* Alpha-68K96II 'BT' (c) 1987 */
        //TODO 	DRIVER( skysoldr )	/* Alpha-68K96II 'SS' (c) 1988 SNK (Romstar with dip switch) */
        //TODO 	DRIVER( goldmedl )	/* Alpha-68K96II 'GM' (c) 1988 SNK */
        //TODO TESTDRIVER( goldmedb )	/* Alpha-68K96II bootleg */
        //TODO 	DRIVER( skyadvnt )	/* Alpha-68K96V  'SA' (c) 1989 Alpha Denshi Co. */
        //TODO 	DRIVER( skyadvnu )	/* Alpha-68K96V  'SA' (c) 1989 SNK of America licensed from Alpha */
        //TODO 	DRIVER( skyadvnj )	/* Alpha-68K96V  'SA' (c) 1989 Alpha Denshi Co. */
        //TODO 	DRIVER( gangwars )	/* Alpha-68K96V       (c) 1989 Alpha Denshi Co. */
        //TODO 	DRIVER( gangwarb )	/* Alpha-68K96V bootleg */
        //TODO 	DRIVER( sbasebal )	/* Alpha-68K96V       (c) 1989 SNK of America licensed from Alpha */
        //TODO 	DRIVER( tnexspce )	/* A8003 'NS' (c) 1989 */
        //TODO 
        //TODO 	/* Technos games */
        //TODO 	DRIVER( scregg )	/* TA-0001 (c) 1983 */
        //TODO 	DRIVER( eggs )		/* TA-0002 (c) 1983 Universal USA */
        //TODO 	DRIVER( dommy )		/* TA-00?? (c) */
        //TODO 	DRIVER( bigprowr )	/* TA-0007 (c) 1983 */
        //TODO 	DRIVER( tagteam )	/* TA-0007 (c) 1983 + Data East license */
        //TODO 	DRIVER( ssozumo )	/* TA-0008 (c) 1984 */
        //TODO 	DRIVER( mystston )	/* TA-0010 (c) 1984 */
        //TODO 	DRIVER( myststno )	/* TA-0010 (c) 1984 */
        //TODO 	DRIVER( dogfgt )	/* TA-0011 (c) 1984 */
        //TODO 	DRIVER( bogeyman )	/* -0204-0 (Data East part number) (c) [1985?] */
        //TODO 	DRIVER( matmania )	/* TA-0015 (c) 1985 + Taito America license */
        //TODO 	DRIVER( excthour )	/* TA-0015 (c) 1985 + Taito license */
        //TODO 	DRIVER( maniach )	/* TA-0017 (c) 1986 + Taito America license */
        //TODO 	DRIVER( maniach2 )	/* TA-0017 (c) 1986 + Taito America license */
        //TODO 	DRIVER( renegade )	/* TA-0018 (c) 1986 + Taito America license */
        //TODO 	DRIVER( kuniokun )	/* TA-0018 (c) 1986 */
        //TODO 	DRIVER( kuniokub )	/* bootleg */
        //TODO 	DRIVER( xsleena )	/* TA-0019 (c) 1986 */
        //TODO 	DRIVER( xsleenab )	/* bootleg */
        //TODO 	DRIVER( solarwar )	/* TA-0019 (c) 1986 Taito + Memetron license */
        //TODO 	DRIVER( battlane )	/* -0215, -0216 (Data East part number) (c) 1986 + Taito license */
        //TODO 	DRIVER( battlan2 )	/* -0215, -0216 (Data East part number) (c) 1986 + Taito license */
        //TODO 	DRIVER( battlan3 )	/* -0215, -0216 (Data East part number) (c) 1986 + Taito license */
        //TODO 	DRIVER( ddragon )	/* TA-0021 (c) 1987 */
        //TODO 	DRIVER( ddragonu )	/* TA-0021 (c) 1987 Taito America */
        //TODO 	DRIVER( ddragonb )	/* bootleg */
        //TODO 	DRIVER( spdodgeb )	/* TA-0022 (c) 1987 */
        //TODO 	DRIVER( nkdodgeb )	/* TA-0022 (c) 1987 (Japan) */
        //TODO 	DRIVER( chinagat )	/* TA-0023 (c) 1988 Taito + Romstar license (US) */
        //TODO 	DRIVER( saiyugou )	/* TA-0023 (c) 1988 (Japan) */
        //TODO 	DRIVER( saiyugb1 )	/* bootleg */
        //TODO 	DRIVER( saiyugb2 )	/* bootleg */
        //TODO 	DRIVER( wwfsstar )	/* TA-0024 (c) 1989 (US) */
        //TODO 	DRIVER( vball )		/* TA-0025 (c) 1988 */
        //TODO 	DRIVER( vball2pj )	/* TA-0025 (c) 1988 (Japan) */
        //TODO 	DRIVER( ddragon2 )	/* TA-0026 (c) 1988 (World) */
        //TODO 	DRIVER( ddragn2u )	/* TA-0026 (c) 1988 (US) */
        //TODO 	DRIVER( toffy )		/* (c) 1993 Midas */
        //TODO 	DRIVER( stoffy )	/* (c) 1994 Midas + Unico */
        //TODO TESTDRIVER( ddungeon )
        //TODO 	DRIVER( ctribe )	/* TA-0028 (c) 1990 (US) */
        //TODO 	DRIVER( ctribeb )	/* bootleg */
        //TODO 	DRIVER( blockout )	/* TA-0029 (c) 1989 + California Dreams */
        //TODO 	DRIVER( blckout2 )	/* TA-0029 (c) 1989 + California Dreams */
        //TODO 	DRIVER( blckoutj )	/* TA-0029 (c) 1989 + California Dreams (Japan) */
        //TODO 	DRIVER( ddragon3 )	/* TA-0030 (c) 1990 */
        //TODO 	DRIVER( ddrago3b )	/* bootleg */
        //TODO 	DRIVER( wwfwfest )	/* TA-0031 (c) 1991 (US) */
        //TODO 	DRIVER( wwfwfsta )	/* TA-0031 (c) 1991 + Tecmo license (US) */
        //TODO 	DRIVER( wwfwfstj )	/* TA-0031 (c) 1991 (Japan) */
        //TODO 	DRIVER( shadfrce )	/* TA-0032 (c) 1993 (US) */
        //TODO 
        //TODO 	/* Stern "Berzerk hardware" games */
        //TODO 	DRIVER( berzerk )	/* (c) 1980 */
        //TODO 	DRIVER( berzerk1 )	/* (c) 1980 */
        //TODO 	DRIVER( frenzy )	/* (c) 1982 */
        //TODO 
        //TODO 	/* GamePlan games */
        //TODO TESTDRIVER( toratora )	/* (c) 1980 Game Plan */
        //TODO 	DRIVER( megatack )	/* (c) 1980 Centuri */
        //TODO 	DRIVER( killcom )	/* (c) 1980 Centuri */
        //TODO 	DRIVER( challeng )	/* (c) 1981 Centuri */
        //TODO 	DRIVER( kaos )		/* (c) 1981 */
        //TODO 
        //TODO 	/* Zaccaria games */
        //TODO 	DRIVER( sia2650 )
        //TODO 	DRIVER( tinv2650 )
        //TODO TESTDRIVER( embargo )
        //TODO 	DRIVER( monymony )	/* (c) 1983 */
        //TODO 	DRIVER( jackrabt )	/* (c) 1984 */
        //TODO 	DRIVER( jackrab2 )	/* (c) 1984 */
        //TODO 	DRIVER( jackrabs )	/* (c) 1984 */
        //TODO 
        //TODO 	/* UPL games */
        //TODO 	DRIVER( mouser )	/* UPL-83001 (c) 1983 */
        //TODO 	DRIVER( mouserc )	/* UPL-83001 (c) 1983 */
        //TODO 	DRIVER( nova2001 )	/* UPL-83005 (c) 1983 */
        //TODO 	DRIVER( nov2001u )	/* UPL-83005 (c) [1983] + Universal license */
        //TODO 	DRIVER( ninjakun )	/* UPL-84003 (c) 1984 Taito Corporation */
        //TODO 	DRIVER( raiders5 )	/* UPL-85004 (c) 1985 */
        //TODO 	DRIVER( raidrs5t )
        //TODO 	DRIVER( pkunwar )	/* UPL-????? [1985?] */
        //TODO 	DRIVER( pkunwarj )	/* UPL-????? [1985?] */
        //TODO 	DRIVER( xxmissio )	/* UPL-86001 [1986] */
        //TODO 	DRIVER( ninjakd2 )	/* UPL-????? (c) 1987 */
        //TODO 	DRIVER( ninjak2a )	/* UPL-????? (c) 1987 */
        //TODO 	DRIVER( ninjak2b )	/* UPL-????? (c) 1987 */
        //TODO 	DRIVER( rdaction )	/* UPL-87003?(c) 1987 + World Games license */
        //TODO 	DRIVER( mnight )	/* UPL-????? (c) 1987 distributed by Kawakus */
        //TODO 	DRIVER( arkarea )	/* UPL-87007 (c) [1988?] */
        //TODO 	DRIVER( robokid )	/* UPL-88013 (c) 1988 */
        //TODO 	DRIVER( robokidj )	/* UPL-88013 (c) 1988 */
        //TODO 	DRIVER( omegaf )	/* UPL-89016 (c) 1989 */
        //TODO 	DRIVER( omegafs )	/* UPL-89016 (c) 1989 */
        //TODO 
        //TODO 	/* UPL/NMK/Banpresto games */
        //TODO TESTDRIVER( urashima )	/* UPL-89052 */
        //TODO TESTDRIVER( tharrier )	/* UPL-89053 (c) 1989 UPL + American Sammy license */
        //TODO 	DRIVER( mustang )	/* UPL-90058 (c) 1990 UPL */
        //TODO 	DRIVER( mustangs )	/* UPL-90058 (c) 1990 UPL + Seoul Trading */
        //TODO 	DRIVER( mustangb )	/* bootleg */
        //TODO 	DRIVER( bioship )	/* UPL-90062 (c) 1990 UPL + American Sammy license */
        //TODO 	DRIVER( vandyke )	/* UPL-90064 (c) UPL */
        //TODO 	DRIVER( blkheart )	/* UPL-91069 */
        //TODO 	DRIVER( blkhearj )	/* UPL-91069 */
        //TODO 	DRIVER( acrobatm )	/* UPL-91073 (c) 1991 UPL + Taito license */
        //TODO 	DRIVER( strahl )	/* UPL-91074 (c) 1992 UPL (Japan) */
        //TODO 	DRIVER( strahla )	/* UPL-91074 (c) 1992 UPL (Japan) */
        //TODO 	DRIVER( bjtwin )	/* UPL-93087 (c) 1993 NMK */
        //TODO 	DRIVER( tdragon2 )	/* UPL-93091 (c) 1993 NMK */
        //TODO 	DRIVER( tdragon )	/* (c) 1991 NMK / Tecmo */
        //TODO 	DRIVER( tdragonb )	/* bootleg */
        //TODO TESTDRIVER( hachamf )	/* (c) 1991 NMK */
        //TODO 	DRIVER( macross )	/* (c) 1992 Banpresto */
        //TODO 	DRIVER( gunnail )	/* (c) 1993 NMK / Tecmo */
        //TODO 	DRIVER( macross2 )	/* (c) 1993 Banpresto */
        //TODO 	DRIVER( sabotenb )	/* (c) 1992 NMK / Tecmo */
        //TODO 	DRIVER( nouryoku )	/* (c) 1995 Tecmo */
        //TODO 	DRIVER( manybloc )	/* (c) 1991 Bee-Oh */
        //TODO 	DRIVER( ssmissin )	/* (c) 1992 Comad */
        //TODO 
        //TODO 	/* don't know what hardare Banpresto used for these games */
        //TODO 	DRIVER( macrossp )	/* (c) 1996 Banpresto */
        //TODO 	DRIVER( quizmoon )	/* (c) 1997 Banpresto */
        //TODO 
        //TODO 	/* Face/NMK games */
        //TODO 	DRIVER( gakupara )	/* (c) 1991 NMK */
        //TODO 	DRIVER( quizdna )	/* (c) 1992 Face */
        //TODO 	DRIVER( gekiretu )	/* (c) 1992 Face */
        //TODO 
        //TODO 	/* Williams/Midway games */
        //TODO 	DRIVER( narc )		/* (c) 1988 Williams */
        //TODO 	DRIVER( narc3 )		/* (c) 1988 Williams */
        //TODO 	DRIVER( trog )		/* (c) 1990 Midway */
        //TODO 	DRIVER( trog3 )		/* (c) 1990 Midway */
        //TODO 	DRIVER( trogpa6 )	/* (c) 1990 Midway */
        //TODO 	DRIVER( trogp )		/* (c) 1990 Midway */
        //TODO 	DRIVER( smashtv )	/* (c) 1990 Williams */
        //TODO 	DRIVER( smashtv6 )	/* (c) 1990 Williams */
        //TODO 	DRIVER( smashtv5 )	/* (c) 1990 Williams */
        //TODO 	DRIVER( smashtv4 )	/* (c) 1990 Williams */
        //TODO 	DRIVER( hiimpact )	/* (c) 1990 Williams */
        //TODO 	DRIVER( shimpact )	/* (c) 1991 Midway */
        //TODO 	DRIVER( shimpacp )	/* (c) 1991 Midway */
        //TODO 	DRIVER( strkforc )	/* (c) 1991 Midway */
        //TODO 	DRIVER( mk )		/* (c) 1992 Midway */
        //TODO 	DRIVER( mkr4 )		/* (c) 1992 Midway */
        //TODO 	DRIVER( mkprot9 )	/* (c) 1992 Midway */
        //TODO 	DRIVER( mkla1 )		/* (c) 1992 Midway */
        //TODO 	DRIVER( mkla2 )		/* (c) 1992 Midway */
        //TODO 	DRIVER( mkla3 )		/* (c) 1992 Midway */
        //TODO 	DRIVER( mkla4 )		/* (c) 1992 Midway */
        //TODO 	DRIVER( term2 )		/* (c) 1992 Midway */
        //TODO 	DRIVER( term2la2 )	/* (c) 1992 Midway */
        //TODO 	DRIVER( totcarn )	/* (c) 1992 Midway */
        //TODO 	DRIVER( totcarnp )	/* (c) 1992 Midway */
        //TODO 	DRIVER( mk2 )		/* (c) 1993 Midway */
        //TODO 	DRIVER( mk2r32 )	/* (c) 1993 Midway */
        //TODO 	DRIVER( mk2r21 )	/* (c) 1993 Midway */
        //TODO 	DRIVER( mk2r14 )	/* (c) 1993 Midway */
        //TODO 	DRIVER( mk2r42 )	/* hack */
        //TODO 	DRIVER( mk2r91 )	/* hack */
        //TODO 	DRIVER( mk2chal )	/* hack */
        //TODO 	DRIVER( jdredd )	/* (c) 1993 Midway */
        //TODO 	DRIVER( nbajam )	/* (c) 1993 Midway */
        //TODO 	DRIVER( nbajamr2 )	/* (c) 1993 Midway */
        //TODO 	DRIVER( nbajamte )	/* (c) 1994 Midway */
        //TODO 	DRIVER( nbajamt1 )	/* (c) 1994 Midway */
        //TODO 	DRIVER( nbajamt2 )	/* (c) 1994 Midway */
        //TODO 	DRIVER( nbajamt3 )	/* (c) 1994 Midway */
        //TODO 	DRIVER( revx )		/* (c) 1994 Midway */
        //TODO 	DRIVER( mk3 )		/* (c) 1994 Midway */
        //TODO 	DRIVER( mk3r20 )	/* (c) 1994 Midway */
        //TODO 	DRIVER( mk3r10 )	/* (c) 1994 Midway */
        //TODO 	DRIVER( umk3 )		/* (c) 1994 Midway */
        //TODO 	DRIVER( umk3r11 )	/* (c) 1994 Midway */
        //TODO 	DRIVER( wwfmania )	/* (c) 1995 Midway */
        //TODO 	DRIVER( openice )	/* (c) 1995 Midway */
        //TODO 	DRIVER( nbahangt )	/* (c) 1996 Midway */
        //TODO 	DRIVER( nbamaxht )	/* (c) 1996 Midway */
        //TODO 	DRIVER( rmpgwt )	/* (c) 1997 Midway */
        //TODO 	DRIVER( rmpgwt11 )	/* (c) 1997 Midway */
        //TODO 	DRIVER( crusnusa )	/* (c) 1994 Midway */
        //TODO 	DRIVER( crusnu40 )	/* (c) 1994 Midway */
        //TODO 	DRIVER( crusnu21 )	/* (c) 1994 Midway */
        //TODO 	DRIVER( crusnwld )	/* (c) 1996 Midway */
        //TODO 	DRIVER( crusnw20 )	/* (c) 1996 Midway */
        //TODO 	DRIVER( crusnw13 )	/* (c) 1996 Midway */
        //TODO 	DRIVER( offroadc )	/* (c) 1997 Midway */
        //TODO 	DRIVER( wargods )	/* (c) 1996 Midway */
        //TODO 
        //TODO 	/* Cinematronics raster games */
        //TODO 	DRIVER( jack )		/* (c) 1982 Cinematronics */
        //TODO 	DRIVER( jack2 )		/* (c) 1982 Cinematronics */
        //TODO 	DRIVER( jack3 )		/* (c) 1982 Cinematronics */
        //TODO 	DRIVER( treahunt )	/* (c) 1982 Hara Ind. */
        //TODO 	DRIVER( zzyzzyxx )	/* (c) 1982 Cinematronics + Advanced Microcomputer Systems */
        //TODO 	DRIVER( zzyzzyx2 )	/* (c) 1982 Cinematronics + Advanced Microcomputer Systems */
        //TODO 	DRIVER( brix )		/* (c) 1982 Cinematronics + Advanced Microcomputer Systems */
        //TODO 	DRIVER( freeze )	/* Cinematronics */
        //TODO 	DRIVER( sucasino )	/* (c) 1982 Data Amusement */
        //TODO 
        //TODO 	/* Cinematronics vector games */
        //TODO 	DRIVER( spacewar )
        //TODO 	DRIVER( barrier )
        //TODO 	DRIVER( starcas )	/* (c) 1980 */
        //TODO 	DRIVER( starcas1 )	/* (c) 1980 */
        //TODO 	DRIVER( starcasp )
        //TODO 	DRIVER( starcase )
        //TODO 	DRIVER( stellcas )
        //TODO 	DRIVER( tailg )
        //TODO 	DRIVER( ripoff )
        //TODO 	DRIVER( armora )
        //TODO 	DRIVER( armorap )
        //TODO 	DRIVER( armorar )
        //TODO 	DRIVER( wotw )
        //TODO 	DRIVER( warrior )
        //TODO 	DRIVER( starhawk )
        //TODO 	DRIVER( solarq )	/* (c) 1981 */
        //TODO 	DRIVER( boxingb )	/* (c) 1981 */
        //TODO 	DRIVER( speedfrk )
        //TODO 	DRIVER( sundance )
        //TODO 	DRIVER( demon )		/* (c) 1982 Rock-ola */
        //TODO 	/* this one uses 68000+Z80 instead of the Cinematronics CPU */
        //TODO 	DRIVER( cchasm )
        //TODO 	DRIVER( cchasm1 )	/* (c) 1983 Cinematronics / GCE */
        //TODO 
        //TODO 	/* "The Pit hardware" games */
        //TODO 	DRIVER( roundup )	/* (c) 1981 Amenip/Centuri */
        //TODO 	DRIVER( fitter )	/* (c) 1981 Taito */
        //TODO 	DRIVER( thepit )	/* (c) 1982 Centuri */
        //TODO 	DRIVER( portman )	/* (c) 1982 Nova Games Ltd. */
        //TODO 	DRIVER( funnymou )	/* (c) 1982 Chuo Co. Ltd */
        //TODO 	DRIVER( suprmous )	/* (c) 1982 Taito */
        //TODO 	DRIVER( machomou )	/* (c) 1982 Techstar */
        //TODO 	DRIVER( intrepid )	/* (c) 1983 Nova Games Ltd. */
        //TODO 	DRIVER( intrepi2 )	/* (c) 1983 Nova Games Ltd. */
        //TODO 	DRIVER( zaryavos )	/* (c) 1983 Nova Games of Canada (prototype) */
        //TODO 	DRIVER( timelimt )	/* (c) 1983 Chuo Co. Ltd */
        //TODO 
        //TODO 	/* Valadon Automation games */
        //TODO 	DRIVER( bagman )	/* (c) 1982 */
        //TODO 	DRIVER( bagnard )	/* (c) 1982 */
        //TODO 	DRIVER( bagmans )	/* (c) 1982 + Stern license */
        //TODO 	DRIVER( bagmans2 )	/* (c) 1982 + Stern license */
        //TODO 	DRIVER( sbagman )	/* (c) 1984 */
        //TODO 	DRIVER( sbagmans )	/* (c) 1984 + Stern license */
        //TODO 	DRIVER( pickin )	/* (c) 1983 */
        //TODO 	DRIVER( tankbust )	/* (c) 1985 */
        //TODO 
        //TODO 	/* Seibu Denshi / Seibu Kaihatsu games */
        //TODO 	DRIVER( stinger )	/* (c) 1983 Seibu Denshi */
        //TODO 	DRIVER( stinger2 )	/* (c) 1983 Seibu Denshi */
        //TODO 	DRIVER( scion )		/* (c) 1984 Seibu Denshi */
        //TODO 	DRIVER( scionc )	/* (c) 1984 Seibu Denshi + Cinematronics license */
        //TODO 	DRIVER( wiz )		/* (c) 1985 Seibu Kaihatsu */
        //TODO 	DRIVER( wizt )		/* (c) 1985 Taito Corporation */
        //TODO 	DRIVER( kncljoe )	/* (c) 1985 Taito Corporation */
        //TODO 	DRIVER( kncljoea )	/* (c) 1985 Taito Corporation */
        //TODO 	DRIVER( bcrusher )	/* bootleg */
        //TODO 	DRIVER( empcity )	/* (c) 1986 Seibu Kaihatsu (bootleg?) */
        //TODO 	DRIVER( empcityj )	/* (c) 1986 Taito Corporation (Japan) */
        //TODO 	DRIVER( stfight )	/* (c) 1986 Seibu Kaihatsu (Germany) (bootleg?) */
        //TODO TESTDRIVER( cshooter )	/* (c) 1987 Taito */
        //TODO TESTDRIVER( cshootre )
        //TODO TESTDRIVER( airraid )
        //TODO 	DRIVER( deadang )	/* (c) 1988 Seibu Kaihatsu */
        //TODO 	DRIVER( ghunter )	/* (c) 1988 Seibu Kaihatsu + Segasa/Sonic license */
        //TODO 	DRIVER( dynduke )	/* (c) 1989 Seibu Kaihatsu + Fabtek license */
        //TODO 	DRIVER( dbldyn )	/* (c) 1989 Seibu Kaihatsu + Fabtek license */
        //TODO 	DRIVER( raiden )	/* (c) 1990 Seibu Kaihatsu */
        //TODO 	DRIVER( raidena )	/* (c) 1990 Seibu Kaihatsu */
        //TODO 	DRIVER( raidenk )	/* (c) 1990 Seibu Kaihatsu + IBL Corporation license */
        //TODO 	DRIVER( raident )	/* (c) 1990 Seibu Kaihatsu + Liang HWA Electronics license */
        //TODO 	DRIVER( sdgndmps )	/* (c) Banpresto / Bandai (Japan) */
        //TODO 	DRIVER( dcon )		/* (c) 1992 Success */
        //TODO 
        //TODO /* Seibu STI System games:
        //TODO 
        //TODO 	Viper: Phase 1 					(c) 1995
        //TODO 	Viper: Phase 1 (New version)	(c) 1996
        //TODO 	Battle Balls					(c) 1996
        //TODO 	Raiden Fighters					(c) 1996
        //TODO 	Raiden Fighters 2 				(c) 1997
        //TODO 	Senku							(c) 1997
        //TODO 
        //TODO */
        //TODO 
        //TODO 	/* Tad games (Tad games run on Seibu hardware) */
        //TODO 	DRIVER( cabal )		/* (c) 1988 Tad + Fabtek license */
        //TODO 	DRIVER( cabal2 )	/* (c) 1988 Tad + Fabtek license */
        //TODO 	DRIVER( cabalbl )	/* bootleg */
        //TODO 	DRIVER( toki )		/* (c) 1989 Tad (World) */
        //TODO 	DRIVER( tokia )		/* (c) 1989 Tad (World) */
        //TODO 	DRIVER( tokij )		/* (c) 1989 Tad (Japan) */
        //TODO 	DRIVER( tokiu )		/* (c) 1989 Tad + Fabtek license (US) */
        //TODO 	DRIVER( tokib )		/* bootleg */
        //TODO 	DRIVER( bloodbro )	/* (c) 1990 Tad */
        //TODO 	DRIVER( weststry )	/* bootleg */
        //TODO 	DRIVER( skysmash )	/* (c) 1990 Nihon System Inc. */
        //TODO TESTDRIVER( legionna )	/* (c) 1992 Tad (World) */
        //TODO TESTDRIVER( legionnu )	/* (c) 1992 Tad + Fabtek license (US) */
        //TODO TESTDRIVER( heatbrl )	/* (c) 1992 Tad (World) */
        //TODO TESTDRIVER( heatbrlo )	/* (c) 1992 Tad (World) */
        //TODO TESTDRIVER( heatbrlu )	/* (c) 1992 Tad (US) */
        //TODO 
        //TODO 	/* Jaleco games */
        //TODO 	DRIVER( exerion )	/* (c) 1983 Jaleco */
        //TODO 	DRIVER( exeriont )	/* (c) 1983 Jaleco + Taito America license */
        //TODO 	DRIVER( exerionb )	/* bootleg */
        //TODO TESTDRIVER( fcombat )	/* (c) 1985 Jaleco */
        //TODO 	DRIVER( formatz )	/* (c) 1984 Jaleco */
        //TODO 	DRIVER( aeroboto )	/* (c) 1984 Williams */
        //TODO 	DRIVER( citycon )	/* (c) 1985 Jaleco */
        //TODO 	DRIVER( citycona )	/* (c) 1985 Jaleco */
        //TODO 	DRIVER( cruisin )	/* (c) 1985 Jaleco/Kitkorp */
        //TODO 	DRIVER( momoko )	/* (c) 1986 Jaleco */
        //TODO 	DRIVER( argus )		/* (c) 1986 Jaleco */
        //TODO 	DRIVER( valtric )	/* (c) 1986 Jaleco */
        //TODO 	DRIVER( butasan )	/* (c) 1987 Jaleco */
        //TODO 	DRIVER( psychic5 )	/* (c) 1987 Jaleco */
        //TODO 	DRIVER( ginganin )	/* (c) 1987 Jaleco */
        //TODO 	DRIVER( skyfox )	/* (c) 1987 Jaleco + Nichibutsu USA license */
        //TODO 	DRIVER( exerizrb )	/* bootleg */
        //TODO 	DRIVER( bigrun )	/* (c) 1989 Jaleco */
        //TODO 	DRIVER( cischeat )	/* (c) 1990 Jaleco */
        //TODO 	DRIVER( f1gpstar )	/* (c) 1991 Jaleco */
        //TODO 	DRIVER( scudhamm )	/* (c) 1994 Jaleco */
        //TODO 	DRIVER( tetrisp2 )	/* (c) 1997 Jaleco */
        //TODO 	DRIVER( teplus2j )	/* (c) 1997 Jaleco */
        //TODO TESTDRIVER( rockn1 )	/* (c) 1999 Jaleco */
        //TODO TESTDRIVER( rockn2 )	/* (c) 1999 Jaleco */
        //TODO TESTDRIVER( rocknms )	/* (c) 1999 Jaleco */
        //TODO TESTDRIVER( rockn3 )	/* (c) 1999 Jaleco */
        //TODO TESTDRIVER( rockn4 )	/* (c) 2000 Jaleco */
        //TODO 
        //TODO 	/* Jaleco Mega System 1 games */
        //TODO 	DRIVER( lomakai )	/* (c) 1988 (World) */
        //TODO 	DRIVER( makaiden )	/* (c) 1988 (Japan) */
        //TODO 	DRIVER( p47 )		/* (c) 1988 */
        //TODO 	DRIVER( p47j )		/* (c) 1988 (Japan) */
        //TODO 	DRIVER( kickoff )	/* (c) 1988 (Japan) */
        //TODO 	DRIVER( tshingen )	/* (c) 1988 (Japan) */
        //TODO 	DRIVER( tshingna )	/* (c) 1988 (Japan) */
        //TODO 	DRIVER( iganinju )	/* (c) 1988 (Japan) */
        //TODO 	DRIVER( astyanax )	/* (c) 1989 */
        //TODO 	DRIVER( lordofk )	/* (c) 1989 (Japan) */
        //TODO 	DRIVER( hachoo )	/* (c) 1989 */
        //TODO 	DRIVER( jitsupro )	/* (c) 1989 (Japan) */
        //TODO 	DRIVER( plusalph )	/* (c) 1989 */
        //TODO 	DRIVER( stdragon )	/* (c) 1989 */
        //TODO 	DRIVER( rodland )	/* (c) 1990 */
        //TODO 	DRIVER( rodlandj )	/* (c) 1990 (Japan) */
        //TODO 	DRIVER( rodlndjb )	/* bootleg */
        //TODO 	DRIVER( avspirit )	/* (c) 1991 */
        //TODO 	DRIVER( phantasm )	/* (c) 1991 (Japan) */
        //TODO 	DRIVER( edf )		/* (c) 1991 */
        //TODO 	DRIVER( 64street )	/* (c) 1991 */
        //TODO 	DRIVER( 64streej )	/* (c) 1991 (Japan) */
        //TODO 	DRIVER( soldamj )	/* (c) 1992 (Japan) */
        //TODO 	DRIVER( bigstrik )	/* (c) 1992 */
        //TODO 	DRIVER( bigstrkb )	/* bootleg on different hardware */
        //TODO 	DRIVER( chimerab )	/* (c) 1993 */
        //TODO 	DRIVER( cybattlr )	/* (c) 1993 */
        //TODO 	DRIVER( peekaboo )	/* (c) 1993 */
        //TODO 
        //TODO 	/* Jaleco Mega System 1 games */
        //TODO 	DRIVER( hayaosi1 )	/* (c) 1994 */
        //TODO 	DRIVER( bbbxing )	/* (c) 1994 */
        //TODO 	DRIVER( desertwr )	/* (c) 1995 */
        //TODO 	DRIVER( gametngk )	/* (c) 1995 */
        //TODO 	DRIVER( tetrisp )	/* (c) 1995 Jaleco / BPS */
        //TODO 	DRIVER( p47aces )	/* (c) 1995 */
        //TODO 	DRIVER( gratia )	/* (c) 1996 */
        //TODO 	DRIVER( gratiaa )	/* (c) 1996 */
        //TODO 	DRIVER( kirarast )	/* (c) 1996 */
        //TODO 	DRIVER( tp2m32 )	/* (c) 1997 */
        //TODO TESTDRIVER( f1superb )	/* (c) 1994 */
        //TODO 
        //TODO 	/* Video System Co. games */
        //TODO 	DRIVER( rabiolep )	/* (c) 1987 V-System Co. (Japan) */
        //TODO 	DRIVER( rpunch )	/* (c) 1987 V-System Co. + Bally/Midway/Sente license (US) */
        //TODO 	DRIVER( svolley )	/* (c) 1989 V-System Co. (Japan) */
        //TODO 	DRIVER( svolleyk )	/* (c) 1989 V-System Co. (Korea) */
        //TODO 	DRIVER( svolleyu )	/* (c) 1989 V-System Co. + Data East license (US) */
        //TODO 	DRIVER( tail2nos )	/* [1989] V-System Co. */
        //TODO 	DRIVER( sformula )	/* [1989] V-System Co. (Japan) */
        //TODO 	DRIVER( ojankoy )	/* [1986] V-System Co. (Japan) */
        //TODO 	DRIVER( ojanko2 )	/* [1987] V-System Co. (Japan) */
        //TODO 	DRIVER( ccasino )	/* [1987] V-System Co. (Japan) */
        //TODO 	DRIVER( ojankohs )	/* [1988] V-System Co. (Japan) */
        //TODO 	DRIVER( nekkyoku )	/* [1988] Video System Co. (Japan) */
        //TODO 	DRIVER( idolmj )	/* [1988] (c) System Service (Japan) */
        //TODO 	DRIVER( mjnatsu )	/* [1989] Video System presents (Japan) */
        //TODO 	DRIVER( mfunclub )	/* [1989] V-System (Japan) */
        //TODO 	DRIVER( daiyogen )	/* [1990] Video System Co. (Japan) */
        //TODO 	DRIVER( nmsengen )	/* (c) 1991 Video System (Japan) */
        //TODO 	DRIVER( fromance )	/* (c) 1991 Video System Co. (Japan) */
        //TODO 	DRIVER( pipedrm )	/* (c) 1990 Video System Co. (US) */
        //TODO 	DRIVER( pipedrmj )	/* (c) 1990 Video System Co. (Japan) */
        //TODO 	DRIVER( hatris )	/* (c) 1990 Video System Co. (Japan) */
        //TODO 	DRIVER( spinlbrk )	/* (c) 1990 V-System Co. (World) */
        //TODO 	DRIVER( spinlbru )	/* (c) 1990 V-System Co. (US) */
        //TODO 	DRIVER( spinlbrj )	/* (c) 1990 V-System Co. (Japan) */
        //TODO 	DRIVER( pspikes )	/* (c) 1991 Video System Co. (World) */
        //TODO 	DRIVER( pspikesk )	/* (c) 1991 Video System Co. (Korea) */
        //TODO 	DRIVER( svolly91 )	/* (c) 1991 Video System Co. (Japan) */
        //TODO 	DRIVER( karatblz )	/* (c) 1991 Video System Co. */
        //TODO 	DRIVER( karatblu )	/* (c) 1991 Video System Co. (US) */
        //TODO 	DRIVER( turbofrc )	/* (c) 1991 Video System Co. */
        //TODO 	DRIVER( aerofgt )	/* (c) 1992 Video System Co. */
        //TODO 	DRIVER( aerofgtb )	/* (c) 1992 Video System Co. */
        //TODO 	DRIVER( aerofgtc )	/* (c) 1992 Video System Co. */
        //TODO 	DRIVER( sonicwi )	/* (c) 1992 Video System Co. (Japan) */
        //TODO 	DRIVER( welltris )	/* (c) 1991 Video System Co. (Japan) */
        //TODO 	DRIVER( f1gp )		/* (c) 1991 Video System Co. */
        //TODO 	DRIVER( f1gp2 )		/* (c) 1992 Video System Co. */
        //TODO 	DRIVER( crshrace )	/* (c) 1993 Video System Co. */
        //TODO 	DRIVER( crshrac2 )	/* (c) 1993 Video System Co. */
        //TODO 	DRIVER( taotaido )	/* (c) 1993 Video System Co. */
        //TODO 	DRIVER( taotaida )	/* (c) 1993 Video System Co. */
        //TODO 	DRIVER( gstriker )	/* (c) [1993] Human */
        //TODO TESTDRIVER( vgoalsoc )
        //TODO TESTDRIVER( vgoalsca )
        //TODO TESTDRIVER( worldc94 )
        //TODO 	DRIVER( suprslam )	/* (c) 1995 Banpresto */
        //TODO 	DRIVER( fromanc2 )	/* (c) 1995 Video System Co. (Japan) */
        //TODO 	DRIVER( fromancr )	/* (c) 1995 Video System Co. (Japan) */
        //TODO 	DRIVER( fromanc4 )	/* (c) 1998 Video System Co. (Japan) */
        //TODO 
        //TODO 	/* Psikyo games */
        //TODO 	DRIVER( samuraia )	/* (c) 1993 (World) */
        //TODO 	DRIVER( sngkace )	/* (c) 1993 (Japan) */
        //TODO 	DRIVER( gunbird )	/* (c) 1994 */
        //TODO 	DRIVER( gunbirdk )	/* (c) 1994 */
        //TODO 	DRIVER( gunbirdj )	/* (c) 1994 */
        //TODO 	DRIVER( btlkroad )	/* (c) 1994 */
        //TODO 	DRIVER( s1945 )		/* (c) 1995 */
        //TODO 	DRIVER( s1945j )	/* (c) 1995 */
        //TODO 	DRIVER( s1945jn )	/* (c) 1995 */
        //TODO 	DRIVER( tengai )	/* (c) 1996 */
        //TODO 	DRIVER( s1945ii )	/* (c) 1997 */
        //TODO 	DRIVER( soldivid )	/* (c) 1997 */
        //TODO 	DRIVER( sbomberb )	/* (c) 1998 */
        //TODO 	DRIVER( daraku )	/* (c) 1998 */
        //TODO 	DRIVER( gunbird2 )	/* (c) 1998 */
        //TODO 	DRIVER( s1945iii )	/* (c) 1999 */
        //TODO 	DRIVER( dragnblz )	/* (c) 2000 */
        //TODO 	DRIVER( gnbarich )	/* (c) 2001 */
        //TODO 	DRIVER( hotgmck )	/* (c) 1997 */
        //TODO 	DRIVER( hgkairak )	/* (c) 1998 */
        //TODO 	DRIVER( loderndf )	/* (c) 2000 */
        //TODO 	DRIVER( loderdfa )	/* (c) 2000 */
        //TODO 	DRIVER( hotdebut )	/* (c) 2000 */
        //TODO 
        //TODO 	/* Orca games */
        //TODO 	DRIVER( marineb )	/* (c) 1982 Orca */
        //TODO 	DRIVER( changes )	/* (c) 1982 Orca */
        //TODO 	DRIVER( looper )	/* (c) 1982 Orca */
        //TODO 	DRIVER( springer )	/* (c) 1982 Orca */
        //TODO 	DRIVER( hoccer )	/* (c) 1983 Eastern Micro Electronics, Inc. */
        //TODO 	DRIVER( hoccer2 )	/* (c) 1983 Eastern Micro Electronics, Inc. */
        //TODO 	DRIVER( bcruzm12 )	/* (c) 1983 Sigma Ent. Inc. */
        //TODO 	DRIVER( hopprobo )	/* (c) 1983 Sega */
        //TODO 	DRIVER( wanted )	/* (c) 1984 Sigma Ent. Inc. */
        //TODO 	DRIVER( funkybee )	/* (c) 1982 Orca */
        //TODO 	DRIVER( skylancr )	/* (c) 1983 Orca + Esco Trading Co license */
        //TODO 	DRIVER( zodiack )	/* (c) 1983 Orca + Esco Trading Co license */
        //TODO 	DRIVER( dogfight )	/* (c) 1983 Thunderbolt */
        //TODO 	DRIVER( moguchan )	/* (c) 1982 Orca + Eastern Commerce Inc. license (doesn't appear on screen) */
        //TODO 	DRIVER( percuss )	/* (c) 1981 Orca */
        //TODO 	DRIVER( bounty )	/* (c) 1982 Orca */
        //TODO 	DRIVER( espial )	/* (c) 1983 Thunderbolt, Orca logo is hidden in title screen */
        //TODO 	DRIVER( espiale )	/* (c) 1983 Thunderbolt, Orca logo is hidden in title screen */
        //TODO 	DRIVER( netwars )	/* (c) 1983 Orca + Esco Trading Co license */
        //TODO 	/* Vastar was made by Orca, but when it was finished, Orca had already bankrupted. */
        //TODO 	/* So they sold this game as "Made by Sesame Japan" because they couldn't use */
        //TODO 	/* the name "Orca" */
        //TODO 	DRIVER( vastar )	/* (c) 1983 Sesame Japan */
        //TODO 	DRIVER( vastar2 )	/* (c) 1983 Sesame Japan */
        //TODO /*
        //TODO    other Orca games:
        //TODO    82 Battle Cross                         Kit 2P
        //TODO    82 River Patrol Empire Mfg/Kerstens Ind Ded 2P        HC Action
        //TODO    82 Slalom                               Kit 2P        HC Action
        //TODO    83 Net Wars                                 2P
        //TODO    83 Super Crush                          Kit 2P           Action
        //TODO */
        //TODO 
        //TODO 	/* Gaelco 2D games */
        //TODO 	/* Master Boy */	/* (c) 1987 - No Ref on the PCB */
        //TODO 	DRIVER( bigkarnk )	/* (c) 1991 - Ref 901112-1 */
        //TODO 	/* Master Boy 2 */	/* (c) 1991 - Ref ??? */
        //TODO 	DRIVER( splash )	/* (c) 1992 - Ref 922804 */
        //TODO 	/*  Thunder Hoop */	/* (c) 1992 - Ref 922804/1 */
        //TODO 	/* Squash */		/* (c) 1992 - Ref 922804/2 */
        //TODO TESTDRIVER( wrally )	/* (c) 1993 - Ref 930705 */
        //TODO TESTDRIVER( wrallya )	/* (c) 1993 - Ref 930705 */
        //TODO 	/* Glass */			/* (c) 1993 - Ref 931021 */
        //TODO 	DRIVER( targeth )	/* (c) 1994 - Ref 940531 */
        //TODO 	DRIVER( thoop2 )	/* (c) 1994 - Ref ??? */
        //TODO 	DRIVER( aligator )	/* (c) 1994 - Ref 940411 */
        //TODO 	DRIVER( aligatun )	/* (c) 1994 - Ref 940411 (unprotected) */
        //TODO 	DRIVER( biomtoy )	/* (c) 1995 - Ref 922804/2 - (unprotected) */
        //TODO TESTDRIVER( touchgo )	/* (c) 1995 - Ref 950510-1 */
        //TODO 	DRIVER( wrally2 )	/* (c) 1995 - Ref 950510 */
        //TODO 	DRIVER( maniacsp )	/* (c) 1996 - Ref 922804/2 - (prototype) */
        //TODO 	DRIVER( maniacsq )	/* (c) 1996 - Ref ??? - (unprotected) */
        //TODO 	DRIVER( snowboar )	/* (c) 1996 - Ref 960419/1 */
        //TODO 	DRIVER( snowbalt )	/* (c) 1996 - Ref 960419/1 */
        //TODO 	DRIVER( bang )		/* (c) 1998 - Ref ??? */
        //TODO 
        //TODO 	/*
        //TODO 	Remaining Gaelco Games:
        //TODO 	=======================
        //TODO 	1996: Speed Up
        //TODO 	1997: Surf Planet (Ref 971223)
        //TODO 	1998: Radikal Bikers
        //TODO 	1999: Rolling Extreme
        //TODO 	2000: Football Power
        //TODO 	2001: Smashing Drive
        //TODO 	2002: ATV Track
        //TODO 	*/
        //TODO 
        //TODO 	/* Kaneko games */
        //TODO 	DRIVER( airbustr )	/* (c) 1990 Kaneko + Namco */
        //TODO 	DRIVER( airbustj )	/* (c) 1990 Kaneko + Namco (Japan) */
        //TODO 	DRIVER( galpanic )	/* (c) 1990 Kaneko */
        //TODO 	DRIVER( galpanib )	/* (c) 1990 Kaneko */
        //TODO TESTDRIVER( galpani2 )	/* (c) 1993 Kaneko */
        //TODO 
        //TODO 	/* Kaneko "AX System" games */
        //TODO 	DRIVER( berlwall )	/* (c) 1991 Kaneko */
        //TODO 	DRIVER( berlwalt )	/* (c) 1991 Kaneko */
        //TODO 	DRIVER( mgcrystl )	/* (c) 1991 Kaneko (World) */
        //TODO 	DRIVER( mgcrystj )	/* (c) 1991 Kaneko + distributed by Atlus (Japan) */
        //TODO 	DRIVER( blazeon )	/* (c) 1992 Atlus */
        //TODO 	DRIVER( sandscrp )	/* (c) 1992 Face */
        //TODO TESTDRIVER( bakubrkr )
        //TODO TESTDRIVER( shogwarr )
        //TODO TESTDRIVER( brapboys )
        //TODO 	DRIVER( gtmr )		/* (c) 1994 Kaneko */
        //TODO 	DRIVER( gtmre )		/* (c) 1994 Kaneko */
        //TODO 	DRIVER( gtmrusa )	/* (c) 1994 Kaneko (US) */
        //TODO 	DRIVER( gtmr2 )		/* (c) 1995 Kaneko */
        //TODO 
        //TODO 	/* Kaneko "Super Nova System" games */
        //TODO 	DRIVER( galpani4 )	/* (c) 1996 Kaneko (Japan) */
        //TODO 	DRIVER( galpanis )	/* (c) 1997 Kaneko (Japan) */
        //TODO 	DRIVER( sengekis )	/* (c) 1997 Kaneko / Warashi (Japan) */
        //TODO 	DRIVER( sarukani )	/* (c) 1997 Kaneko (Japan) */
        //TODO 	DRIVER( cyvern )	/* (c) 1998 Kaneko (Japan) */
        //TODO 	DRIVER( galpans2 )	/* (c) 1999 Kaneko (Japan) */
        //TODO 	DRIVER( panicstr )	/* (c) 1999 Kaneko (Japan) */
        //TODO 	DRIVER( senknow )	/* (c) 1999 Kaneko (Japan) */
        //TODO TESTDRIVER( gutsn )
        //TODO 	DRIVER( puzzloop )	/* (c) 1998 Mitchell (Japan) */
        //TODO 	DRIVER( puzloopj )	/* (c) 1998 Mitchell (Japan) */
        //TODO 	DRIVER( jjparads )	/* (c) 1996 Electro Design Co. (Japan) */
        //TODO 	DRIVER( jjparad2 )	/* (c) 1997 Electro Design Co. (Japan) */
        //TODO 	DRIVER( ryouran )	/* (c) 1998 Electro Design Co. (Japan) */
        //TODO 	DRIVER( teljan )	/* (c) 1999 Electro Design Co. (Japan) */
        //TODO 
        //TODO 	/* Seta games */
        //TODO 	DRIVER( hanaawas )	/* (c) SetaKikaku */
        //TODO 	DRIVER( srmp2 )		/* UB or UC?? (c) 1987 */
        //TODO 	DRIVER( srmp3 )		/* ZA-0? (c) 1988 */
        //TODO 	DRIVER( mjyuugi )	/* (c) 1990 Visco */
        //TODO 	DRIVER( mjyuugia )	/* (c) 1990 Visco */
        //TODO 	DRIVER( ponchin )	/* (c) 1991 Visco */
        //TODO 	DRIVER( ponchina )	/* (c) 1991 Visco */
        //TODO 	DRIVER( tndrcade )	/* UA-0 (c) 1987 Taito */
        //TODO 	DRIVER( tndrcadj )	/* UA-0 (c) 1987 Taito */
        //TODO 	DRIVER( twineagl )	/* UA-2 (c) 1988 + Taito license */
        //TODO 	DRIVER( downtown )	/* UD-2 (c) 1989 + Romstar or Taito license (DSW) */
        //TODO 	DRIVER( usclssic )	/* UE   (c) 1989 + Romstar or Taito license (DSW) */
        //TODO 	DRIVER( calibr50 )	/* UH   (c) 1989 + Romstar or Taito license (DSW) */
        //TODO 	DRIVER( arbalest )	/* UK   (c) 1989 + Jordan, Romstar or Taito license (DSW) */
        //TODO 	DRIVER( metafox )	/* UP   (c) 1989 + Jordan, Romstar or Taito license (DSW) */
        //TODO 	DRIVER( drgnunit )	/* (c) 1989 Athena / Seta + Romstar or Taito license (DSW) */
        //TODO 	DRIVER( wits )		/* (c) 1989 Athena (Visco license) */
        //TODO 	DRIVER( thunderl )	/* (c) 1990 Seta + Romstar or Visco license (DSW) */
        //TODO 	DRIVER( rezon )		/* (c) 1991 Allumer */
        //TODO 	DRIVER( stg )		/* (c) 1991 Athena / Tecmo */
        //TODO 	DRIVER( blandia )	/* (c) 1992 Allumer */
        //TODO 	DRIVER( blandiap )	/* (c) 1992 Allumer */
        //TODO 	DRIVER( blockcar )	/* (c) 1992 Visco */
        //TODO 	DRIVER( qzkklogy )	/* (c) 1992 Tecmo */
        //TODO 	DRIVER( umanclub )	/* (c) 1992 Tsuburaya Prod. / Banpresto */
        //TODO 	DRIVER( zingzip )	/* UY   (c) 1992 Allumer + Tecmo */
        //TODO 	DRIVER( atehate )	/* (C) 1993 Athena */
        //TODO 	DRIVER( jjsquawk )	/* (c) 1993 Athena / Able */
        //TODO 	DRIVER( kamenrid )	/* (c) 1993 Toei / Banpresto */
        //TODO 	DRIVER( madshark )	/* (c) 1993 Allumer */
        //TODO 	DRIVER( msgundam )	/* (c) 1993 Banpresto */
        //TODO 	DRIVER( msgunda1 )	/* (c) 1993 Banpresto */
        //TODO 	DRIVER( daioh )		/* (C) 1993 Athena */
        //TODO 	DRIVER( oisipuzl )	/* (c) 1993 SunSoft / Atlus */
        //TODO 	DRIVER( triplfun )	/* bootleg */
        //TODO 	DRIVER( utoukond )	/* (c) 1993 Banpresto + Tsuburaya Prod. */
        //TODO 	DRIVER( qzkklgy2 )	/* (c) 1993 Tecmo */
        //TODO 	DRIVER( wrofaero )	/* (c) 1993 Yang Cheng */
        //TODO 	DRIVER( eightfrc )	/* (c) 1994 Tecmo */
        //TODO 	DRIVER( kiwame )	/* (c) 1994 Athena */
        //TODO 	DRIVER( krzybowl )	/* (c) 1994 American Sammy */
        //TODO 	DRIVER( extdwnhl )	/* (c) 1995 Sammy Japan */
        //TODO 	DRIVER( gundhara )	/* (c) 1995 Banpresto */
        //TODO 	DRIVER( sokonuke )	/* (c) 1995 Sammy Industries */
        //TODO 	DRIVER( zombraid )	/* (c) 1995 American Sammy */
        //TODO 
        //TODO 	DRIVER( grdians )	/* (c) 1995 Banpresto */
        //TODO 	DRIVER( mj4simai )	/* (c) 1996 Maboroshi Ware */
        //TODO 	DRIVER( myangel )	/* (c) 1996 Namco */
        //TODO 	DRIVER( myangel2 )	/* (c) 1997 Namco */
        //TODO 	DRIVER( pzlbowl )	/* (c) 1999 Nihon System / Moss */
        //TODO 	DRIVER( penbros )	/* (c) 2000 Subsino */
        //TODO 
        //TODO 	/* SSV System (Seta, Sammy, Visco) games */
        //TODO 	DRIVER( srmp4 )		/* (c) 1993 Seta */
        //TODO TESTDRIVER( twineag2 )	/* (c) 1994 Seta */
        //TODO 	DRIVER( srmp7 )		/* (c) 1997 Seta */
        //TODO 	DRIVER( survarts )	/* (c) 1993 Sammy (American) */
        //TODO TESTDRIVER( eaglshot )
        //TODO TESTDRIVER( eaglshta )
        //TODO 	DRIVER( hypreact )	/* (c) 1995 Sammy */
        //TODO 	DRIVER( meosism )	/* (c) Sammy */
        //TODO 	DRIVER( hypreac2 )	/* (c) 1997 Sammy */
        //TODO 	DRIVER( sxyreact )	/* (c) 1998 Sammy */
        //TODO 	DRIVER( cairblad )	/* (c) 1999 Sammy */
        //TODO 	DRIVER( keithlcy )	/* (c) 1993 Visco */
        //TODO 	DRIVER( drifto94 )	/* (c) 1994 Visco */
        //TODO 	DRIVER( janjans1 )	/* (c) 1996 Visco */
        //TODO 	DRIVER( stmblade )	/* (c) 1996 Visco */
        //TODO 	DRIVER( mslider )	/* (c) 1997 Visco / Datt Japan */
        //TODO 	DRIVER( ryorioh )	/* (c) 1998 Visco */
        //TODO 
        //TODO 	/* Atlus games */
        //TODO 	DRIVER( powerins )	/* (c) 1993 Atlus (Japan) */
        //TODO 	DRIVER( ohmygod )	/* (c) 1993 Atlus (Japan) */
        //TODO 	DRIVER( naname )	/* (c) 1994 Atlus (Japan) */
        //TODO 	DRIVER( blmbycar )	/* (c) 1994 ABM & Gecas - uses same gfx chip as powerins? */
        //TODO 	DRIVER( blmbycau )	/* (c) 1994 ABM & Gecas - uses same gfx chip as powerins? */
        //TODO 
        //TODO 	/* Sun Electronics / SunSoft games */
        //TODO 	DRIVER( speakres )	/* [Sun Electronics] */
        //TODO 	DRIVER( stratvox )	/* [1980 Sun Electronics] Taito */
        //TODO 	DRIVER( spacecho )	/* bootleg */
        //TODO 	DRIVER( route16 )	/* (c) 1981 Tehkan/Sun + Centuri license */
        //TODO 	DRIVER( route16b )	/* bootleg */
        //TODO 	DRIVER( ttmahjng )	/* Taito */
        //TODO 	DRIVER( fnkyfish )	/* (c) 1981 Sun Electronics */
        //TODO 	DRIVER( kangaroo )	/* (c) 1982 Sun Electronics */
        //TODO 	DRIVER( kangaroa )	/* 136008			(c) 1982 Atari */
        //TODO 	DRIVER( kangarob )	/* (bootleg) */
        //TODO 	DRIVER( arabian )	/* TVG13 (c) 1983 Sun Electronics */
        //TODO 	DRIVER( arabiana )	/* 136019			(c) 1983 Atari */
        //TODO 	DRIVER( markham )	/* TVG14 (c) 1983 Sun Electronics */
        //TODO 	DRIVER( strnskil )	/* TVG15 (c) 1984 Sun Electronics */
        //TODO 	DRIVER( guiness )	/* TVG15 (c) 1984 Sun Electronics */
        //TODO 	DRIVER( pettanp )	/* TVG16 (c) 1984 Sun Electronics (Japan) */
        //TODO 	DRIVER( ikki )		/* TVG17 (c) 1985 Sun Electronics (Japan) */
        //TODO 	DRIVER( shanghai )	/* (c) 1988 Sunsoft (Sun Electronics) */
        //TODO 	DRIVER( shangha2 )	/* (c) 1989 Sunsoft (Sun Electronics) */
        //TODO 	DRIVER( shangha3 )	/* (c) 1993 Sunsoft */
        //TODO 	DRIVER( heberpop )	/* (c) 1994 Sunsoft / Atlus */
        //TODO 	DRIVER( blocken )	/* (c) 1994 KID / Visco */
        //TODO /*
        //TODO Other Sun games
        //TODO 1978 (GT)Block Perfect
        //TODO 1978 (GT)Block Challenger
        //TODO 1979 Galaxy Force
        //TODO 1979 Run Away
        //TODO 1979 Dai San Wakusei (The Third Planet)
        //TODO 1979 Warp 1
        //TODO 1980 Cosmo Police (Cosmopolis?)
        //TODO 1985 Ikki
        //TODO 1993 Saikyou Battler Retsuden
        //TODO 1995 Shanghai Banri no Choujou (ST-V)
        //TODO 1996 Karaoke Quiz Intro DonDon (ST-V)
        //TODO 1998 Astra Super Stars (ST-V)
        //TODO 1998 Shanghai Mateki Buyuu (TPS)
        //TODO */
        //TODO 
        //TODO 	/* Suna games */
        //TODO 	DRIVER( goindol )	/* (c) 1987 Sun a Electronics */
        //TODO 	DRIVER( goindolu )	/* (c) 1987 Sun a Electronics */
        //TODO 	DRIVER( goindolj )	/* (c) 1987 Sun a Electronics */
        //TODO 	DRIVER( rranger )	/* (c) 1988 SunA + Sharp Image license */
        //TODO TESTDRIVER( sranger )	/* (c) 1988 SunA */
        //TODO TESTDRIVER( srangerb )	/* bootleg */
        //TODO TESTDRIVER( srangerw )
        //TODO 	DRIVER( hardhead )	/* (c) 1988 SunA */
        //TODO 	DRIVER( hardhedb )	/* bootleg */
        //TODO TESTDRIVER( starfigh )	/* (c) 1990 SunA */
        //TODO TESTDRIVER( hardhea2 )
        //TODO TESTDRIVER( brickzn )
        //TODO TESTDRIVER( brickzn3 )
        //TODO 	DRIVER( bssoccer )	/* (c) 1996 SunA */
        //TODO 	DRIVER( uballoon )	/* (c) 1996 SunA */
        //TODO 
        //TODO 	/* Dooyong games */
        //TODO 	DRIVER( gundealr )	/* (c) 1990 Dooyong */
        //TODO 	DRIVER( gundeala )	/* (c) Dooyong */
        //TODO 	DRIVER( gundealt )	/* (c) 1990 Tecmo */
        //TODO 	DRIVER( yamyam )	/* (c) 1990 Dooyong */
        //TODO 	DRIVER( wiseguy )	/* (c) 1990 Dooyong */
        //TODO 	DRIVER( lastday )	/* (c) 1990 Dooyong */
        //TODO 	DRIVER( lastdaya )	/* (c) 1990 Dooyong */
        //TODO 	DRIVER( gulfstrm )	/* (c) 1991 Dooyong */
        //TODO 	DRIVER( gulfstr2 )	/* (c) 1991 Dooyong + distributed by Media Shoji */
        //TODO 	DRIVER( pollux )	/* (c) 1991 Dooyong */
        //TODO 	DRIVER( bluehawk )	/* (c) 1993 Dooyong */
        //TODO 	DRIVER( bluehawn )	/* (c) 1993 NTC */
        //TODO 	DRIVER( sadari )	/* (c) 1993 NTC */
        //TODO 	DRIVER( gundl94 )	/* (c) 1994 Dooyong */
        //TODO 	DRIVER( primella )	/* (c) 1994 NTC */
        //TODO 	DRIVER( rshark )	/* (c) 1995 Dooyong */
        //TODO 
        //TODO 	/* Tong Electronic games */
        //TODO 	DRIVER( leprechn )	/* (c) 1982 */
        //TODO 	DRIVER( potogold )	/* (c) 1982 */
        //TODO 	DRIVER( beezer )	/* (c) 1982 */
        //TODO 	DRIVER( beezer1 )	/* (c) 1982 */
        //TODO 
        //TODO 	/* Comad games */
        //TODO 	DRIVER( pushman )	/* (c) 1990 Comad + American Sammy license */
        //TODO 	DRIVER( bballs )	/* (c) 1991 Comad */
        //TODO 	DRIVER( zerozone )	/* (c) 1993 Comad */
        //TODO 	DRIVER( lvgirl94 )	/* (c) 1994 Comad */
        //TODO 	DRIVER( hotpinbl )	/* (c) 1995 Comad & New Japan System */
        //TODO 	DRIVER( galspnbl )	/* (c) 1996 Comad */
        //TODO 	/* the following ones run on modified Gals Panic hardware */
        //TODO 	DRIVER( fantasia )	/* (c) 1994 Comad & New Japan System */
        //TODO 	DRIVER( newfant )	/* (c) 1995 Comad & New Japan System */
        //TODO 	DRIVER( fantsy95 )	/* (c) 1995 Hi-max Technology Inc. */
        //TODO 	DRIVER( missw96 )	/* (c) 1996 Comad */
        //TODO 	DRIVER( fantsia2 )	/* (c) 1997 Comad */
        //TODO 
        //TODO 	/* Playmark games */
        //TODO 	DRIVER( sslam )		/* (c) 1993 */
        //TODO 	DRIVER( bigtwin )	/* (c) 1995 */
        //TODO 	DRIVER( wbeachvl )	/* (c) 1995 */
        //TODO 
        //TODO 	/* Pacific Novelty games */
        //TODO 	DRIVER( sharkatt )	/* (c) [1980] */
        //TODO 	DRIVER( thief )		/* (c) 1981 */
        //TODO 	DRIVER( natodef )	/* (c) 1982 */
        //TODO 	DRIVER( natodefa )	/* (c) 1982 */
        //TODO 	DRIVER( mrflea )	/* (c) 1982 */
        //TODO 
        //TODO 	/* Tecfri games */
        //TODO 	DRIVER( holeland )	/* (c) 1984 */
        //TODO 	DRIVER( crzrally )	/* (c) 1985 */
        //TODO 	DRIVER( speedbal )	/* (c) 1987 */
        //TODO 	DRIVER( sauro )		/* (c) 1987 */
        //TODO 
        //TODO 	/* Metro games */
        //TODO 	DRIVER( karatour )	/* (c) Mitchell */
        //TODO 	DRIVER( ladykill )	/* Yanyaka + Mitchell license */
        //TODO 	DRIVER( moegonta )	/* Yanyaka (Japan) */
        //TODO 	DRIVER( pangpoms )	/* (c) 1992 */
        //TODO 	DRIVER( pangpomm )	/* (c) 1992 Mitchell / Metro */
        //TODO 	DRIVER( skyalert )	/* (c) 1992 */
        //TODO 	DRIVER( poitto )	/* (c) 1993 Metro / Able Corp. */
        //TODO 	DRIVER( dharma )	/* (c) 1994 */
        //TODO 	DRIVER( lastfort )	/* (c) */
        //TODO 	DRIVER( lastfero )	/* (c) */
        //TODO 	DRIVER( toride2g )	/* (c) 1994 */
        //TODO 	DRIVER( daitorid )	/* (c) */
        //TODO 	DRIVER( dokyusei )	/* (c) 1995 Make Software / Elf / Media Trading */
        //TODO 	DRIVER( dokyusp )	/* (c) 1995 Make Software / Elf / Media Trading */
        //TODO 	DRIVER( puzzli )	/* (c) Metro / Banpresto */
        //TODO 	DRIVER( 3kokushi )	/* (c) 1996 Mitchell */
        //TODO 	DRIVER( pururun )	/* (c) 1995 Metro / Banpresto */
        //TODO 	DRIVER( balcube )	/* (c) 1996 */
        //TODO 	DRIVER( mouja )		/* (c) 1996 Etona (Japan) */
        //TODO 	DRIVER( bangball )	/* (c) 1996 Banpresto / Kunihiko Tashiro+Goodhouse */
        //TODO 	DRIVER( gakusai )	/* (c) 1997 MakeSoft */
        //TODO 	DRIVER( gakusai2 )	/* (c) 1998 MakeSoft */
        //TODO 	DRIVER( blzntrnd )	/* (c) 1994 Human Amusement */
        //TODO 	DRIVER( hyprduel )	/* (c) 1993 Technosoft (World) */
        //TODO 	DRIVER( hyprdelj )	/* (c) 1993 Technosoft (Japan) */
        //TODO 
        //TODO 	/* Venture Line games */
        //TODO 	DRIVER( spcforce )	/* (c) 1980 Venture Line */
        //TODO 	DRIVER( spcforc2 )	/* bootleg */
        //TODO 	DRIVER( meteor )	/* (c) 1981 Venture Line */
        //TODO 	DRIVER( looping )	/* (c) 1982 Venture Line + licensed from Video Games */
        //TODO 	DRIVER( loopinga )	/* (c) 1982 Venture Line + licensed from Video Games */
        //TODO 	DRIVER( skybump )	/* (c) 1982 Venture Line */
        //TODO 
        //TODO 	/* Yun Sung games */
        //TODO 	DRIVER( cannball )	/* (c) 1995 Yun Sung / Soft Visio */
        //TODO 	DRIVER( magix )		/* (c) 1995 Yun Sung */
        //TODO 	DRIVER( magicbub )	/* (c) Yun Sung */
        //TODO 	DRIVER( shocking )	/* (c) 1997 Yun Sung */
        //TODO 
        //TODO 	/* Zilec games */
        //TODO 	DRIVER( blueprnt )	/* (c) 1982 Bally Midway (Zilec in ROM 3U, and the programmer names) */
        //TODO 	DRIVER( blueprnj )	/* (c) 1982 Jaleco (Zilec in ROM 3U, and the programmer names) */
        //TODO 	DRIVER( saturn )	/* (c) 1983 Jaleco (Zilec in ROM R6, and the programmer names) */
        //TODO 
        //TODO 	/* Fuuki games */
        //TODO 	DRIVER( gogomile )	/* (c) 1995 */
        //TODO 	DRIVER( gogomilj )	/* (c) 1995 (Japan) */
        //TODO 	DRIVER( pbancho )	/* (c) 1996 (Japan) */
        //TODO 
        //TODO 	/* Unico games */
        //TODO 	DRIVER( drgnmst )	/* (c) 1994 */
        //TODO 	DRIVER( burglarx )	/* (c) 1997 */
        //TODO 	DRIVER( zeropnt )	/* (c) 1998 */
        //TODO 	DRIVER( silkroad )	/* (c) 1999 */
        //TODO 
        //TODO 	/* Afega games */
        //TODO 	DRIVER( stagger1 )	/* (c) 1998 */
        //TODO 	DRIVER( redhawk )	/* (c) 1997 */
        //TODO 	DRIVER( grdnstrm )	/* (c) 1998 */
        //TODO 	DRIVER( bubl2000 )	/* (c) 1998 Tuning */
        //TODO 
        //TODO 	/* ESD games */
        //TODO 	/* http://www.esdgame.co.kr/english/ */
        //TODO 	DRIVER( multchmp )	/* (c) 1998 (Korea) */
        //TODO 
        //TODO 	/* Dyna Electronics / Dynax / Nakanihon games */
        //TODO 	DRIVER( royalmah )	/* (c) 1982 Falcon */
        //TODO 	DRIVER( suzume )	/*  ??  (c) 1986 Dyna Electronics */
        //TODO 	DRIVER( hnayayoi )	/* "02" (c) 1987 Dyna Electronics */
        //TODO 	DRIVER( dondenmj )	/* "03" (c) 1986 Dyna Electronics */
        //TODO 	DRIVER( hnfubuki )	/* "06" (c) 1987 Dynax */
        //TODO 	DRIVER( mjdiplob )	/* "07" (c) 1987 Dynax */
        //TODO 	DRIVER( untoucha )	/* "08" (c) 1987 Dynax */
        //TODO 	DRIVER( tontonb )	/* "09" (c) 1987 Dynax */
        //TODO 	DRIVER( hanamai )	/* "16" (c) 1988 Dynax */
        //TODO 	DRIVER( majs101b )	/* "17" (c) [1988] Dynax */
        //TODO 	DRIVER( hnkochou )	/* "20" (c) 1989 Dynax */
        //TODO 	DRIVER( mjderngr )	/* "22" (c) 1989 Dynax */
        //TODO TESTDRIVER( hnoridur )	/* "23" (c) 1989 Dynax */
        //TODO 	DRIVER( drgpunch )	/* "24" (c) 1989 Dynax */
        //TODO 	DRIVER( mjfriday )	/* "26" (c) [1989] Dynax */
        //TODO 						/* "27" Jantouki 1989 Dynax */
        //TODO TESTDRIVER( mjifb )		/* "29" 1990 Dynax */
        //TODO 	DRIVER( sprtmtch )	/* "31" (c) 1989 Dynax + Fabtek license */
        //TODO 	DRIVER( maya )		/* (c) 1994 Promat */
        //TODO TESTDRIVER( ladyfrog )
        //TODO TESTDRIVER( ladyfrga )
        //TODO 						/* "33" Mahjong Campus Hunting 1990 Dynax */
        //TODO 						/* "37" 7jigen no Youseitachi 1990 Dynax */
        //TODO 						/* "45" Neruton Haikujiradan 1990 Dynax */
        //TODO 	DRIVER( mjdialq2 )	/* "52" (c) 1991 Dynax */
        //TODO 						/* "55" Mahjong Yarunara 1991 Dynax */
        //TODO 						/* "61" Mahjong Angels 1991 Dynax */
        //TODO 						/* "64" Quiz TV Gassyuukoku Q&Q 1992 Dynax */
        //TODO TESTDRIVER( mmpanic )	/* "70" 1992 Nakanihon/Taito */
        //TODO 	DRIVER( quizchq )	/* "73" (c) 1993 Nakanihon */
        //TODO 	DRIVER( quizchql )	/* "73" (c) 1993 Laxan */
        //TODO TESTDRIVER( quiz365 )	/* "78" (c) 1994 Nakanihon */
        //TODO 	DRIVER( rongrong )	/* "80" (c) 1994 Nakanihon */
        //TODO 	DRIVER( ddenlovr )	/* "113" (c) 1996 Dynax */
        //TODO TESTDRIVER( hanakanz )	/* "507" 1996 Dynax */
        //TODO 						/* "510" Hana Kagerou 1996 Nakanihon */
        //TODO 						/* "523" Billiard Academy Real Break 1998 */
        //TODO 						/* "526" Mahjong Reach Ippatsu 1998 Nihon System/Dynax */
        //TODO 
        //TODO 	/* Sigma games */
        //TODO 	DRIVER( nyny )		/* (c) 1980 Sigma Ent. Inc. */
        //TODO 	DRIVER( nynyg )		/* (c) 1980 Sigma Ent. Inc. + Gottlieb */
        //TODO 	DRIVER( arcadia )	/* (c) 1982 Sigma Ent. Inc. */
        //TODO 	DRIVER( spiders )	/* (c) 1981 Sigma Ent. Inc. */
        //TODO 	DRIVER( spiders2 )	/* (c) 1981 Sigma Ent. Inc. */
        //TODO 
        //TODO 	/* IGS games */
        //TODO 	DRIVER( iqblock )	/* (c) 1993 */
        //TODO TESTDRIVER( cabaret )
        //TODO 	DRIVER( orlegend )	/* (c) 1997 */
        //TODO 	DRIVER( orlegnde )	/* (c) 1997 */
        //TODO 	DRIVER( orlegndc )	/* (c) 1997 */
        //TODO 	DRIVER( dragwld2 )	/* (c) 1997 */
        //TODO 	DRIVER( kov )		/* (c) 1999 */
        //TODO 	DRIVER( kovplus )	/* (c) 1999 */
        //TODO 	DRIVER( kov115 )	/* (c) 1999 */
        //TODO TESTDRIVER( kovsh )		/* (c) 1999 */
        //TODO 
        //TODO 	/* RamTek games */
        //TODO 	DRIVER( hitme )		/* [1976 Ramtek] */
        //TODO 	DRIVER( barricad )	/* [1976 Ramtek] */
        //TODO 	DRIVER( brickyrd )	/* [1976 Ramtek] */
        //TODO 	DRIVER( starcrus )	/* [1977 Ramtek] */
        //TODO 
        //TODO 	/* Omori games */
        //TODO 	DRIVER( battlex )	/* (c) 1982 Omori E. Co., Ltd. */
        //TODO 	DRIVER( carjmbre )	/* (c) 1983 Omori Electric Co., Ltd. */
        //TODO 	DRIVER( popper )	/* (c) 1983 Omori Electric Co., Ltd. */
        //TODO 
        //TODO 	/* TCH games */
        //TODO 	DRIVER( speedspn )	/* (c) 1994 */
        //TODO 	DRIVER( kickgoal )	/* (c) 1995 */
        //TODO 
        //TODO 	/* U.S. Games games */
        //TODO 	DRIVER( usg32 )
        //TODO 	DRIVER( usg82 )
        //TODO 	DRIVER( usg83 )
        //TODO 	DRIVER( usg83x )
        //TODO 	DRIVER( usg185 )
        //TODO 	DRIVER( usg252 )
        //TODO 
        //TODO 	/* Sanritsu games */
        //TODO 	DRIVER( mermaid )	/* (c) 1982 Rock-ola */	/* made by Sanritsu */
        //TODO 	DRIVER( drmicro )	/* (c) 1983 Sanritsu */
        //TODO 	DRIVER( appoooh )	/* (c) 1984 Sega */	/* made by Sanritsu */
        //TODO 	DRIVER( bankp )		/* (c) 1984 Sega */	/* made by Sanritsu */
        //TODO 	DRIVER( mjkjidai )	/* (c) 1986 Sanritsu */
        //TODO 	DRIVER( mayumi )	/* (c) 1988 Victory L.L.C. */	/* made by Sanritsu */
        //TODO 
        //TODO 	/* Rare games */
        //TODO 	DRIVER( btoads )	/* (c) 1994 Rare */
        //TODO 	DRIVER( kinst )		/* (c) 1994 Rare */
        //TODO 	DRIVER( kinst2 )	/* (c) 1994 Rare */
        //TODO 
        //TODO 	/* Nihon System games */
        //TODO 	DRIVER( gigasb )
        //TODO 	DRIVER( gigasm2b )
        //TODO 	DRIVER( oigas )
        //TODO 	DRIVER( pbillrd )	/* (c) 1987 Nihon System */
        //TODO 	DRIVER( pbillrds )
        //TODO 	DRIVER( freekick )
        //TODO 	DRIVER( freekckb )	/* (c) 1987 Nihon System (+ optional Sega) */
        //TODO TESTDRIVER( countrun )
        //TODO 
        //TODO 	/* Alba games */
        //TODO 	DRIVER( rmhaihai )	/* (c) 1985 Alba */
        //TODO 	DRIVER( rmhaihib )	/* (c) 1985 Alba */
        //TODO 	DRIVER( rmhaijin )	/* (c) 1986 Alba */
        //TODO 	DRIVER( rmhaisei )	/* (c) 1986 Visco */
        //TODO 	DRIVER( themj )		/* (c) 1987 Visco */
        //TODO 	DRIVER( hanaroku )	/* (c) 1988 Alba */
        //TODO 
        //TODO 	/* Home Data games */
        //TODO 	DRIVER( hourouki ) 	/* (c) 1987 Home Data */
        //TODO 	DRIVER( mhgaiden ) 	/* (c) 1987 Home Data */
        //TODO 	DRIVER( mjhokite ) 	/* (c) 1988 Home Data */
        //TODO 	DRIVER( mjclinic ) 	/* (c) 1988 Home Data */
        //TODO 	DRIVER( mrokumei ) 	/* (c) 1988 Home Data */
        //TODO 	DRIVER( reikaids ) 	/* (c) 1988 Home Data */
        //TODO 	DRIVER( mjkojink ) 	/* (c) 1989 Home Data */
        //TODO 	DRIVER( vitaminc ) 	/* (c) 1989 Home Data */
        //TODO 	DRIVER( mjyougo ) 	/* (c) 1989 Home Data */
        //TODO 	DRIVER( lemnangl ) 	/* (c) 1990 Home Data */
        //TODO 	DRIVER( mjkinjas ) 	/* (c) 1991 Home Data */
        //TODO 	DRIVER( jogakuen )	/* Windom corporation */
        //TODO 	DRIVER( mjikaga )	/* Mitchell */
        //TODO 
        //TODO 	/* Art & Magic games */
        //TODO 	DRIVER( ultennis )	/* (c) 1993 */
        //TODO 	DRIVER( cheesech )	/* (c) 1994 */
        //TODO 	DRIVER( stonebal )	/* (c) 1994 */
        //TODO 	DRIVER( stoneba2 )	/* (c) 1994 */
        //TODO 
        //TODO 	/* Taiyo games */
        //TODO 	DRIVER( dynamski )	/* (c) 1984 Taiyo */
        //TODO 	DRIVER( chinhero )	/* (c) 1984 Taiyo */
        //TODO 	DRIVER( shangkid )	/* (c) 1985 Taiyo + Data East license */
        //TODO 	DRIVER( hiryuken )	/* (c) 1985 Taito */
        //TODO 
        //TODO 	DRIVER( astinvad )	/* (c) 1980 Stern */
        //TODO 	DRIVER( kamikaze )	/* Leijac Corporation */
        //TODO 	DRIVER( spcking2 )
        //TODO 	DRIVER( spaceint )	/* [1980] Shoei */
        //TODO 	DRIVER( spacefb )	/* (c) [1980?] Nintendo */
        //TODO 	DRIVER( spacefbg )	/* 834-0031 (c) 1980 Gremlin */
        //TODO 	DRIVER( spacefbb )	/* bootleg */
        //TODO 	DRIVER( spacebrd )	/* bootleg */
        //TODO 	DRIVER( spacedem )	/* (c) 1980 Fortrek + made by Nintendo */
        //TODO 	DRIVER( omegrace )	/* (c) 1981 Midway */
        //TODO 	DRIVER( dday )		/* (c) 1982 Olympia */
        //TODO 	DRIVER( ddayc )		/* (c) 1982 Olympia + Centuri license */
        //TODO 	DRIVER( hexa )		/* D. R. Korea */
        //TODO 	DRIVER( stactics )	/* [1981 Sega] */
        //TODO 	DRIVER( exterm )	/* (c) 1989 Premier Technology - a Gottlieb game */
        //TODO 	DRIVER( kingofb )	/* (c) 1985 Woodplace Inc. */
        //TODO 	DRIVER( ringking )	/* (c) 1985 Data East USA */
        //TODO 	DRIVER( ringkin2 )	/* (c) 1985 Data East USA */
        //TODO 	DRIVER( ringkin3 )	/* (c) 1985 Data East USA */
        //TODO 	DRIVER( ambush )	/* (c) 1983 Nippon Amuse Co-Ltd */
        //TODO 	DRIVER( homo )		/* bootleg */
        //TODO TESTDRIVER( dlair )
        //TODO 	DRIVER( aztarac )	/* (c) 1983 Centuri (vector game) */
        //TODO 	DRIVER( mole )		/* (c) 1982 Yachiyo Electronics, Ltd. */
        //TODO 	DRIVER( thehand )	/* (c) 1981 T.I.C. */
        //TODO 	DRIVER( gotya )		/* (c) 1981 Game-A-Tron */
        //TODO 	DRIVER( mrjong )	/* (c) 1983 Kiwako */
        //TODO 	DRIVER( crazyblk )	/* (c) 1983 Kiwako + ECI license */
        //TODO 	DRIVER( polyplay )
        //TODO 	DRIVER( amspdwy )	/* no copyright notice, but (c) 1987 Enerdyne Technologies, Inc. */
        //TODO 	DRIVER( amspdwya )	/* no copyright notice, but (c) 1987 Enerdyne Technologies, Inc. */
        //TODO 	DRIVER( othldrby )	/* (c) 1995 Sunwise */
        //TODO 	DRIVER( mosaic )	/* (c) 1990 Space */
        //TODO 	DRIVER( mosaica )	/* (c) 1990 Space + Fuuki license */
        //TODO 	DRIVER( gfire2 )	/* (c) 1992 Topis Corp */
        //TODO TESTDRIVER( spdbuggy )
        //TODO 	DRIVER( sprcros2 )	/* (c) 1986 GM Shoji */
        //TODO 	DRIVER( mugsmash )	/* (c) Electronic Devices (Italy) / 3D Games (England) */
        //TODO 	DRIVER( stlforce )	/* (c) 1994 Electronic Devices (Italy) / Ecogames S.L. (Spain) */
        //TODO 	DRIVER( gcpinbal )	/* (c) 1994 Excellent System */
        //TODO 	DRIVER( aquarium )	/* (c) 1996 Excellent System */
        //TODO 	DRIVER( policetr )	/* (c) 1996 P&P Marketing */
        //TODO 	DRIVER( policeto )	/* (c) 1996 P&P Marketing */
        //TODO 	DRIVER( sshooter )	/* (c) 1998 P&P Marketing */
        //TODO 	DRIVER( pass )		/* (c) 1992 Oksan */
        //TODO 	DRIVER( news )		/* "Virus"??? ((c) 1993 Poby in ROM VIRUS.4) */
        //TODO 	DRIVER( taxidrvr )	/* [1984 Graphic Techno] */
        //TODO 	DRIVER( xyonix )	/* [1989 Philko] */
        //TODO 	DRIVER( findout )	/* (c) 1987 [Elettronolo] */
        //TODO 	DRIVER( dribling )	/* (c) 1983 Model Racing */
        //TODO 	DRIVER( driblino )	/* (c) 1983 Olympia */
        //TODO 	DRIVER( ace )		/* [1976 Allied Leisure] */
        //TODO 	DRIVER( clayshoo )	/* [1979 Allied Leisure] */
        //TODO 	DRIVER( pirates )	/* (c) 1994 NIX */
        //TODO 	DRIVER( fitfight )	/* bootleg of Art of Fighting */
        //TODO 	DRIVER( histryma )	/* bootleg of Fighter's History */
        //TODO TESTDRIVER( bbprot )
        //TODO 	DRIVER( flower )	/* (c) 1986 Komax */
        //TODO 	DRIVER( diverboy )	/* (c) 1992 Electronic Devices */
        //TODO 	DRIVER( beaminv )	/* Tekunon Kougyou */
        //TODO 	DRIVER( mcatadv )	/* (c) 1993 Wintechno */
        //TODO 	DRIVER( mcatadvj )	/* (c) 1993 Wintechno */
        //TODO 	DRIVER( nost )		/* (c) 1993 Face */
        //TODO 	DRIVER( nostj )		/* (c) 1993 Face */
        //TODO 	DRIVER( nostk )		/* (c) 1993 Face */
        //TODO 	DRIVER( 4enraya )	/* (c) 1990 IDSA */
        //TODO 	DRIVER( oneshot )	/* no copyright notice */
        //TODO 	DRIVER( maddonna )	/* (c) 1995 Tuning */
        //TODO 	DRIVER( maddonnb )	/* (c) 1995 Tuning */
        //TODO 	DRIVER( tugboat )	/* (c) 1982 ETM */
        //TODO 	DRIVER( gotcha )	/* (c) 1997 Dongsung + "presented by Para" */
        //TODO TESTDRIVER( amerdart )	/* (c) 1989 Ameri Corporation */
        //TODO TESTDRIVER( coolpool )	/* (c) 1992 Catalina Games */
        //TODO TESTDRIVER( 9ballsht )	/* (c) 1993 E-Scape EnterMedia + "marketed by Bundra Games" */
        //TODO TESTDRIVER( 9ballsh2 )	/* (c) 1993 E-Scape EnterMedia + "marketed by Bundra Games" */
        //TODO TESTDRIVER( 9ballsh3 )	/* (c) 1993 E-Scape EnterMedia + "marketed by Bundra Games" */
        //TODO 	DRIVER( gumbo )		/* (c) 1994 Min Corp. */
        //TODO 	DRIVER( trivquiz )	/* (c) 1984 Status Games */
        //TODO 	DRIVER( statriv2 )	/* (c) 1984 Status Games */
        //TODO 	DRIVER( supertr2 )	/* (c) 1986 Status Games */
        //TODO 	DRIVER( tickee )	/* (c) 1994 Raster Elite */
        //TODO 	DRIVER( crgolf )	/* (c) 1984 Nasco Japan */
        //TODO 	DRIVER( crgolfa )	/* (c) 1984 Nasco Japan */
        //TODO 	DRIVER( crgolfb )	/* (c) 1984 Nasco Japan */
        //TODO 
        //TODO 
        //TODO #endif /* CPSMAME */
        //TODO #endif /* NEOMAME */
        //TODO #ifndef CPSMAME
        //TODO 
        //TODO 	/* Neo Geo games */
        //TODO 	/* the four digits number is the game ID stored at address 0x0108 of the program ROM */
        //TODO 	/* info on prototypes taken from http://www.members.tripod.com/fresa/proto/puzzle.htm */
        //TODO 	DRIVER( nam1975 )	/* 0001 (c) 1990 SNK */
        //TODO 	DRIVER( bstars )	/* 0002 (c) 1990 SNK */
        //TODO 	DRIVER( tpgolf )	/* 0003 (c) 1990 SNK */
        //TODO 	DRIVER( mahretsu )	/* 0004 (c) 1990 SNK */
        //TODO 	DRIVER( maglord )	/* 0005 (c) 1990 Alpha Denshi Co. */
        //TODO 	DRIVER( maglordh )	/* 0005 (c) 1990 Alpha Denshi Co. */
        //TODO 	DRIVER( ridhero )	/* 0006 (c) 1990 SNK */
        //TODO 	DRIVER( ridheroh )	/* 0006 (c) 1990 SNK */
        //TODO 	DRIVER( alpham2 )	/* 0007 (c) 1991 SNK */
        //TODO 	/* 0008 Sunshine (prototype) 1990 SNK */
        //TODO 	DRIVER( ncombat )	/* 0009 (c) 1990 Alpha Denshi Co. */
        //TODO 	DRIVER( cyberlip )	/* 0010 (c) 1990 SNK */
        //TODO 	DRIVER( superspy )	/* 0011 (c) 1990 SNK */
        //TODO 	/* 0012 */
        //TODO 	/* 0013 */
        //TODO 	DRIVER( mutnat )	/* 0014 (c) 1992 SNK */
        //TODO 	/* 0015 */
        //TODO 	DRIVER( kotm )		/* 0016 (c) 1991 SNK */
        //TODO 	DRIVER( sengoku )	/* 0017 (c) 1991 SNK */
        //TODO 	DRIVER( sengokh )	/* 0017 (c) 1991 SNK */
        //TODO 	DRIVER( burningf )	/* 0018 (c) 1991 SNK */
        //TODO 	DRIVER( burningh )	/* 0018 (c) 1991 SNK */
        //TODO 	DRIVER( lbowling )	/* 0019 (c) 1990 SNK */
        //TODO 	DRIVER( gpilots )	/* 0020 (c) 1991 SNK */
        //TODO 	DRIVER( joyjoy )	/* 0021 (c) 1990 SNK */
        //TODO 	DRIVER( bjourney )	/* 0022 (c) 1990 Alpha Denshi Co. */
        //TODO 	DRIVER( quizdais )	/* 0023 (c) 1991 SNK */
        //TODO 	DRIVER( lresort )	/* 0024 (c) 1992 SNK */
        //TODO 	DRIVER( eightman )	/* 0025 (c) 1991 SNK / Pallas */
        //TODO 	/* 0026 Fun Fun Brothers (prototype) 1991 Alpha */
        //TODO 	DRIVER( minasan )	/* 0027 (c) 1990 Monolith Corp. */
        //TODO 	/* 0028 Dunk Star (prototype) Sammy */
        //TODO 	DRIVER( legendos )	/* 0029 (c) 1991 SNK */
        //TODO 	DRIVER( 2020bb )	/* 0030 (c) 1991 SNK / Pallas */
        //TODO 	DRIVER( 2020bbh )	/* 0030 (c) 1991 SNK / Pallas */
        //TODO 	DRIVER( socbrawl )	/* 0031 (c) 1991 SNK */
        //TODO 	DRIVER( roboarmy )	/* 0032 (c) 1991 SNK */
        //TODO 	DRIVER( fatfury1 )	/* 0033 (c) 1991 SNK */
        //TODO 	DRIVER( fbfrenzy )	/* 0034 (c) 1992 SNK */
        //TODO 	/* 0035 Mystic Wand (prototype) 1991 Alpha */
        //TODO 	DRIVER( bakatono )	/* 0036 (c) 1991 Monolith Corp. */
        //TODO 	DRIVER( crsword )	/* 0037 (c) 1991 Alpha Denshi Co. */
        //TODO 	DRIVER( trally )	/* 0038 (c) 1991 Alpha Denshi Co. */
        //TODO 	DRIVER( kotm2 )		/* 0039 (c) 1992 SNK */
        //TODO 	DRIVER( sengoku2 )	/* 0040 (c) 1993 SNK */
        //TODO 	DRIVER( bstars2 )	/* 0041 (c) 1992 SNK */
        //TODO 	DRIVER( quizdai2 )	/* 0042 (c) 1992 SNK */
        //TODO 	DRIVER( 3countb )	/* 0043 (c) 1993 SNK */
        //TODO 	DRIVER( aof )		/* 0044 (c) 1992 SNK */
        //TODO 	DRIVER( samsho )	/* 0045 (c) 1993 SNK */
        //TODO 	DRIVER( tophuntr )	/* 0046 (c) 1994 SNK */
        //TODO 	DRIVER( fatfury2 )	/* 0047 (c) 1992 SNK */
        //TODO 	DRIVER( janshin )	/* 0048 (c) 1994 Aicom */
        //TODO 	DRIVER( androdun )	/* 0049 (c) 1992 Visco */
        //TODO 	DRIVER( ncommand )	/* 0050 (c) 1992 Alpha Denshi Co. */
        //TODO 	DRIVER( viewpoin )	/* 0051 (c) 1992 Sammy */
        //TODO 	DRIVER( ssideki )	/* 0052 (c) 1992 SNK */
        //TODO 	DRIVER( wh1 )		/* 0053 (c) 1992 Alpha Denshi Co. */
        //TODO 	/* 0054 Crossed Swords 2  (CD only? not confirmed, MVS might exist) */
        //TODO 	DRIVER( kof94 )		/* 0055 (c) 1994 SNK */
        //TODO 	DRIVER( aof2 )		/* 0056 (c) 1994 SNK */
        //TODO 	DRIVER( wh2 )		/* 0057 (c) 1993 ADK */
        //TODO 	DRIVER( fatfursp )	/* 0058 (c) 1993 SNK */
        //TODO 	DRIVER( savagere )	/* 0059 (c) 1995 SNK */
        //TODO 	DRIVER( fightfev )	/* 0060 (c) 1994 Viccom */
        //TODO 	DRIVER( ssideki2 )	/* 0061 (c) 1994 SNK */
        //TODO 	DRIVER( spinmast )	/* 0062 (c) 1993 Data East Corporation */
        //TODO 	DRIVER( samsho2 )	/* 0063 (c) 1994 SNK */
        //TODO 	DRIVER( wh2j )		/* 0064 (c) 1994 ADK / SNK */
        //TODO 	DRIVER( wjammers )	/* 0065 (c) 1994 Data East Corporation */
        //TODO 	DRIVER( karnovr )	/* 0066 (c) 1994 Data East Corporation */
        //TODO 	DRIVER( gururin )	/* 0067 (c) 1994 Face */
        //TODO 	DRIVER( pspikes2 )	/* 0068 (c) 1994 Video System Co. */
        //TODO 	DRIVER( fatfury3 )	/* 0069 (c) 1995 SNK */
        //TODO 	/* 0070 Zupapa - released in 2001, see below */
        //TODO 	/* 0071 Bang Bang Busters (prototype) 1994 Visco */
        //TODO 	/* 0072 Last Odyssey Pinball Fantasia (prototype) 1995 Monolith */
        //TODO 	DRIVER( panicbom )	/* 0073 (c) 1994 Eighting / Hudson */
        //TODO 	DRIVER( aodk )		/* 0074 (c) 1994 ADK / SNK */
        //TODO 	DRIVER( sonicwi2 )	/* 0075 (c) 1994 Video System Co. */
        //TODO 	DRIVER( zedblade )	/* 0076 (c) 1994 NMK */
        //TODO 	/* 0077 The Warlocks of the Fates (prototype) 1995 Astec */
        //TODO 	DRIVER( galaxyfg )	/* 0078 (c) 1995 Sunsoft */
        //TODO 	DRIVER( strhoop )	/* 0079 (c) 1994 Data East Corporation */
        //TODO 	DRIVER( quizkof )	/* 0080 (c) 1995 Saurus */
        //TODO 	DRIVER( ssideki3 )	/* 0081 (c) 1995 SNK */
        //TODO 	DRIVER( doubledr )	/* 0082 (c) 1995 Technos */
        //TODO 	DRIVER( pbobblen )	/* 0083 (c) 1994 Taito */
        //TODO 	DRIVER( kof95 )		/* 0084 (c) 1995 SNK */
        //TODO 	/* 0085 Shinsetsu Samurai Spirits Bushidoretsuden / Samurai Shodown RPG (CD only) */
        //TODO 	DRIVER( tws96 )		/* 0086 (c) 1996 Tecmo */
        //TODO 	DRIVER( samsho3 )	/* 0087 (c) 1995 SNK */
        //TODO 	DRIVER( stakwin )	/* 0088 (c) 1995 Saurus */
        //TODO 	DRIVER( pulstar )	/* 0089 (c) 1995 Aicom */
        //TODO 	DRIVER( whp )		/* 0090 (c) 1995 ADK / SNK */
        //TODO 	/* 0091 */
        //TODO 	DRIVER( kabukikl )	/* 0092 (c) 1995 Hudson */
        //TODO 	DRIVER( neobombe )	/* 0093 (c) 1997 Hudson */
        //TODO 	DRIVER( gowcaizr )	/* 0094 (c) 1995 Technos */
        //TODO 	DRIVER( rbff1 )		/* 0095 (c) 1995 SNK */
        //TODO 	DRIVER( aof3 )		/* 0096 (c) 1996 SNK */
        //TODO 	DRIVER( sonicwi3 )	/* 0097 (c) 1995 Video System Co. */
        //TODO 	/* 0098 Idol Mahjong - final romance 2 (CD only? not confirmed, MVS might exist) */
        //TODO 	/* 0099 Neo Pool Masters */
        //TODO 	DRIVER( turfmast )	/* 0200 (c) 1996 Nazca */
        //TODO 	DRIVER( mslug )		/* 0201 (c) 1996 Nazca */
        //TODO 	DRIVER( puzzledp )	/* 0202 (c) 1995 Taito (Visco license) */
        //TODO 	DRIVER( mosyougi )	/* 0203 (c) 1995 ADK / SNK */
        //TODO 	/* 0204 QP (prototype) */
        //TODO 	/* 0205 Neo-Geo CD Special (CD only) */
        //TODO 	DRIVER( marukodq )	/* 0206 (c) 1995 Takara */
        //TODO 	DRIVER( neomrdo )	/* 0207 (c) 1996 Visco */
        //TODO 	DRIVER( sdodgeb )	/* 0208 (c) 1996 Technos */
        //TODO 	DRIVER( goalx3 )	/* 0209 (c) 1995 Visco */
        //TODO 	/* 0210 Karate Ninja Sho (prototype) 1995 Yumekobo */
        //TODO 	/* 0211 Oshidashi Zintrick (CD only? not confirmed, MVS might exist) 1996 SNK/ADK */
        //TODO 	DRIVER( overtop )	/* 0212 (c) 1996 ADK */
        //TODO 	DRIVER( neodrift )	/* 0213 (c) 1996 Visco */
        //TODO 	DRIVER( kof96 )		/* 0214 (c) 1996 SNK */
        //TODO 	DRIVER( ssideki4 )	/* 0215 (c) 1996 SNK */
        //TODO 	DRIVER( kizuna )	/* 0216 (c) 1996 SNK */
        //TODO 	DRIVER( ninjamas )	/* 0217 (c) 1996 ADK / SNK */
        //TODO 	DRIVER( ragnagrd )	/* 0218 (c) 1996 Saurus */
        //TODO 	DRIVER( pgoal )		/* 0219 (c) 1996 Saurus */
        //TODO 	/* 0220 Choutetsu Brikin'ger - iron clad (MVS existance seems to have been confirmed) */
        //TODO 	DRIVER( magdrop2 )	/* 0221 (c) 1996 Data East Corporation */
        //TODO 	DRIVER( samsho4 )	/* 0222 (c) 1996 SNK */
        //TODO 	DRIVER( rbffspec )	/* 0223 (c) 1996 SNK */
        //TODO 	DRIVER( twinspri )	/* 0224 (c) 1996 ADK */
        //TODO 	DRIVER( wakuwak7 )	/* 0225 (c) 1996 Sunsoft */
        //TODO 	/* 0226 Pair Pair Wars (prototype) 1996 Sunsoft? */
        //TODO 	DRIVER( stakwin2 )	/* 0227 (c) 1996 Saurus */
        //TODO 	/* 0228 GhostLop (prototype) 1996? Data East */
        //TODO 	/* 0229 King of Fighters '96 CD Collection (CD only) */
        //TODO 	DRIVER( breakers )	/* 0230 (c) 1996 Visco */
        //TODO 	DRIVER( miexchng )	/* 0231 (c) 1997 Face */
        //TODO 	DRIVER( kof97 )		/* 0232 (c) 1997 SNK */
        //TODO 	DRIVER( magdrop3 )	/* 0233 (c) 1997 Data East Corporation */
        //TODO 	DRIVER( lastblad )	/* 0234 (c) 1997 SNK */
        //TODO 	DRIVER( puzzldpr )	/* 0235 (c) 1997 Taito (Visco license) */
        //TODO 	DRIVER( irrmaze )	/* 0236 (c) 1997 SNK / Saurus */
        //TODO 	DRIVER( popbounc )	/* 0237 (c) 1997 Video System Co. */
        //TODO 	DRIVER( shocktro )	/* 0238 (c) 1997 Saurus */
        //TODO 	DRIVER( shocktrj )	/* 0238 (c) 1997 Saurus */
        //TODO 	DRIVER( blazstar )	/* 0239 (c) 1998 Yumekobo */
        //TODO 	DRIVER( rbff2 )		/* 0240 (c) 1998 SNK */
        //TODO 	DRIVER( mslug2 )	/* 0241 (c) 1998 SNK */
        //TODO 	DRIVER( kof98 )		/* 0242 (c) 1998 SNK */
        //TODO 	DRIVER( lastbld2 )	/* 0243 (c) 1998 SNK */
        //TODO 	DRIVER( neocup98 )	/* 0244 (c) 1998 SNK */
        //TODO 	DRIVER( breakrev )	/* 0245 (c) 1998 Visco */
        //TODO 	DRIVER( shocktr2 )	/* 0246 (c) 1998 Saurus */
        //TODO 	DRIVER( flipshot )	/* 0247 (c) 1998 Visco */
        //TODO 	DRIVER( pbobbl2n )	/* 0248 (c) 1999 Taito (SNK license) */
        //TODO 	DRIVER( ctomaday )	/* 0249 (c) 1999 Visco */
        //TODO 	DRIVER( mslugx )	/* 0250 (c) 1999 SNK */
        //TODO 	DRIVER( kof99 )		/* 0251 (c) 1999 SNK */
        //TODO 	DRIVER( kof99e )	/* 0251 (c) 1999 SNK */
        //TODO 	DRIVER( kof99n )	/* 0251 (c) 1999 SNK */
        //TODO 	DRIVER( kof99p )	/* 0251 (c) 1999 SNK */
        //TODO 	DRIVER( ganryu )	/* 0252 (c) 1999 Visco */
        //TODO 	DRIVER( garou )		/* 0253 (c) 1999 SNK */
        //TODO 	DRIVER( garouo )	/* 0253 (c) 1999 SNK */
        //TODO 	DRIVER( garoup )	/* 0253 (c) 1999 SNK */
        //TODO 	DRIVER( s1945p )	/* 0254 (c) 1999 Psikyo */
        //TODO 	DRIVER( preisle2 )	/* 0255 (c) 1999 Yumekobo */
        //TODO 	DRIVER( mslug3 )	/* 0256 (c) 2000 SNK */
        //TODO 	DRIVER( mslug3n )	/* 0256 (c) 2000 SNK */
        //TODO 	DRIVER( kof2000 )	/* 0257 (c) 2000 SNK */
        //TODO 	DRIVER( kof2000n )	/* 0257 (c) 2000 SNK */
        //TODO 	/* 0258 SNK vs. Capcom? (prototype) */
        //TODO 	DRIVER( bangbead )	/* 0259 (c) 2000 Visco (prototype) */
        //TODO 	DRIVER( nitd )		/* 0260 (c) 2000 Eleven / Gavaking */
        //TODO 	DRIVER( zupapa )	/* 0070 (c) 2001 SNK */
        //TODO 	DRIVER( sengoku3 )	/* 0261 (c) 2001 SNK */
        //TODO 	DRIVER( kof2001 )	/* 0262 (c) 2001 Eolith / SNK */
        //TODO TESTDRIVER( mslug4 )	/* 0263 (c) 2002 Mega Enterprise / Playmore Corporation */
        //TODO TESTDRIVER( rotd )		/* 0264 (c) 2002 Evoga / Playmore Corporation */
        //TODO TESTDRIVER( kof2002 )	/* 0265 (c) 2002 Eolith / Playmore Corporation */
        //TODO 
        //TODO #endif /* CPSMAME */
        //TODO 
        //TODO #endif	/* DRIVER_RECURSIVE */
        //TODO 
        //TODO #endif	/* TINY_COMPILE */
        //TODO     
        null
    };
}
