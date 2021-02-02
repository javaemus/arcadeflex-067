/* A simple rcfile and commandline parsing mechanism

   Copyright 1999,2000 Hans de Goede

   This file and the acompanying files in this directory are free software;
   you can redistribute them and/or modify them under the terms of the GNU
   Library General Public License as published by the Free Software Foundation;
   either version 2 of the License, or (at your option) any later version.

   These files are distributed in the hope that they will be useful,
   but WITHOUT ANY WARRANTY; without even the implied warranty of
   MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the GNU
   Library General Public License for more details.

   You should have received a copy of the GNU Library General Public
   License along with these files; see the file COPYING.LIB.  If not,
   write to the Free Software Foundation, Inc., 59 Temple Place - Suite 330,
   Boston, MA 02111-1307, USA.
*/

/*
 * ported to v0.67
 * using automatic conversion tool v0.01
 */ 
package gr.codebb.arcadeflex.v067.platform;

public class rcH
{
    public static abstract interface arg_callbackPtr {
        public abstract int handler(String arg);
    }

/*TODO*///	struct rc_struct;
/*TODO*///	struct rc_option;
/*TODO*///	
/*TODO*///	enum { 
    public static final int rc_ignore     = -1;
    public static final int rc_end        = 0;
    public static final int rc_bool       = 1;
    public static final int rc_string     = 2;
    public static final int rc_int        = 3;
    public static final int rc_float      = 4;
    public static final int rc_set_int    = 5;
    public static final int rc_seperator  = 6;
    public static final int rc_file       = 7;
    public static final int rc_use_function   = 8;
    public static final int rc_use_function_no_arg     = 9;
    public static final int rc_link       = 10;

/*TODO*///	typedef int(*rc_func)(struct rc_option *option, const char *arg,
/*TODO*///	   int priority);
    public static abstract interface rc_func {
        public abstract int handler(rc_option option, String arg, int priority);
    }
    
    public static abstract interface rc_assign_func {
        public abstract void handler(int value);
    }

	public static class rc_option
	{
	   String name;  /* name of the option */
	   String shortname;  /* shortcut name of the option, or clear for bool */
	   int type=0;          /* type of the option */
	   Object dest;        /* ptr to where the value of the option should be stored */
	   String deflt; /* default value of the option in a c-string */
	   int min;         /* used to verify rc_int or rc_float, this check is not */
	   int max;         /* done if min == max. min is also used as value for
	                         set_int, and as write flag for rc_file. */
	   rc_func func;      /* function which is called for additional verification
	                         of the value, or which is called to parse the value if
	                         type == use_function, or NULL. Should return 0 on
	                         success, -1 on failure */
	   String help;  /* help text for this option */
	   int priority;      /* priority of the current value, the current value
	                         is only changed when the priority of the source
	                         is higher as this, and then the priority is set to
	                         the priority of the source */
           
           public rc_option()
           {
               
           }
           
           public rc_option(String name, String shortname, int type, Object dest, String deflt, int min, int max, rc_func func, String help/*, int priority*/) 
           {
                this.name = name;
                this.shortname = shortname;
                this.type = type;
                this.dest = dest;
                this.deflt = deflt;
                this.min = min;
                this.max = max;
                this.func = func;
                this.help = help;
                this.priority = priority;
           }
           
	};
	
/*TODO*///	/* open / close */
/*TODO*///	struct rc_struct *rc_create(void);
/*TODO*///	void rc_destroy(struct rc_struct *rc);
/*TODO*///	
/*TODO*///	/* register / unregister */
/*TODO*///	int rc_register(struct rc_struct *rc, struct rc_option *option);
/*TODO*///	int rc_unregister(struct rc_struct *rc, struct rc_option *option);
/*TODO*///	
/*TODO*///	/* load/save (read/write) a configfile */
/*TODO*///	int rc_load(struct rc_struct *rc, const char *name, int priority,
/*TODO*///	   int continue_on_errors);
/*TODO*///	int rc_save(struct rc_struct *rc, const char *name, int append);
/*TODO*///	int osd_rc_read(struct rc_struct *rc, mame_file *f, const char *description,
/*TODO*///	   int priority, int continue_on_errors);
/*TODO*///	int rc_read(struct rc_struct *rc, FILE *f, const char *description,
/*TODO*///	   int priority, int continue_on_errors);
/*TODO*///	int rc_write(struct rc_struct *rc, FILE *f, const char *description);
/*TODO*///	
/*TODO*///	/* commandline handling */
/*TODO*///	int rc_parse_commandline(struct rc_struct *rc, int argc, char *argv[],
/*TODO*///	   int priority, int(*arg_callback)(char *arg));
/*TODO*///	int rc_get_non_option_args(struct rc_struct *rc, int *argc, char **argv[]);
/*TODO*///	
/*TODO*///	/* print help */
/*TODO*///	void rc_print_help(struct rc_struct *rc, FILE *f);
/*TODO*///	
/*TODO*///	/* print commandline options in manpage style */
/*TODO*///	void rc_print_man_options(struct rc_struct *rc, FILE *f);
/*TODO*///	
/*TODO*///	/* some default verify functions */
/*TODO*///	int rc_verify_power_of_2(struct rc_option *option, const char *arg,
/*TODO*///	   int priority);
/*TODO*///	
/*TODO*///	/* functions which can be used in option functions or to build your own
/*TODO*///	   parser. */
/*TODO*///	/* 3 ways to query if an option needs arguments, to query it's priority and
/*TODO*///	   to set it:
/*TODO*///	   -by name, searching the options in a rc instance
/*TODO*///	   -by name, searching an array of options, as given to rc_register
/*TODO*///	   -using the option given (which could for example have been returned
/*TODO*///	    by rc_get_option) */
/*TODO*///	int rc_option_requires_arg(struct rc_struct *rc, const char *name);
/*TODO*///	int rc_option_requires_arg2(struct rc_option *option, const char *name);
/*TODO*///	int rc_option_requires_arg3(struct rc_option *option);
/*TODO*///	
/*TODO*///	int rc_get_priority(struct rc_struct *rc, const char *name);
/*TODO*///	int rc_get_priority2(struct rc_option *option, const char *name);
/*TODO*///	int rc_get_priority3(struct rc_option *option);
/*TODO*///	
/*TODO*///	int rc_set_option(struct rc_struct *rc, const char *name, const char *arg,
/*TODO*///	   int priority);
/*TODO*///	int rc_set_option2(struct rc_option *option, const char *name,
/*TODO*///	   const char *arg, int priority);
/*TODO*///	int rc_set_option3(struct rc_option *option, const char *arg, int priority);
/*TODO*///	
/*TODO*///	/* 2 ways to get the option_struct belonging to a certain option:
/*TODO*///	   -by name, searching the options in a rc instance
/*TODO*///	   -by name, searching an array of options, as given to rc_register */
/*TODO*///	struct rc_option *rc_get_option(struct rc_struct *rc, const char *name);
/*TODO*///	struct rc_option *rc_get_option2(struct rc_option *option, const char *name);
/*TODO*///	
/*TODO*///	/* gimmi the entire tree, I want todo all the parsing myself */
/*TODO*///	struct rc_option *rc_get_options(struct rc_struct *rc);
/*TODO*///	
/*TODO*///	/* various utility functions which don't really belong to the rc object,
/*TODO*///	   but seem to fit here well */
/*TODO*///	int rc_check_and_create_dir(const char *name);
/*TODO*///	char *rc_get_home_dir(void);
/*TODO*///	
/*TODO*///	#endif /* ifndef __RC_H */
}
