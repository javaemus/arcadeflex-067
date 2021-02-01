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
/* Changelog
Version 0.1, December 1999
-Initial release (Hans de Goede)
Version 0.2, January 2000
-Fixed priority parsing for booleans (Hans de Goede)
-Fixed error messages for: "error optionx requires an argument". (Hans de
 Goede)
-Fixed --boolean option parsing. (Hans de Goede)
Version 0.3, Februari 2000
-Reworked and cleaned up the interface, broke backward compatibility (Hans
 de Goede)
*/
/*
 * ported to v0.67
 * using automatic conversion tool v0.01
 */ 
package gr.codebb.arcadeflex.v067.platform;

import static gr.codebb.arcadeflex.common.libc.*;
import static gr.codebb.arcadeflex.v067.platform.rcH.*;

public class rc
{
/*TODO*///	//
/*TODO*///	
/*TODO*///	#ifdef _MSC_VER
/*TODO*///	#define snprintf _snprintf
/*TODO*///	#endif
/*TODO*///	#define BUF_SIZE 512

	public static class rc_struct
	{
	   rc_option[] option;
	   int option_size;
/*TODO*///	   char **arg;
/*TODO*///	   int arg_size;
/*TODO*///	   int args_registered;
	};

/*TODO*///	/* private variables */
/*TODO*///	static int rc_requires_arg[] = {0, 1, 1, 1, 1, 0, 0, 1, 1, 0, 0 };
/*TODO*///	
/*TODO*///	/* private methods */
/*TODO*///	static int rc_verify(struct rc_option *option, float value)
/*TODO*///	{
/*TODO*///	   if(option->min == option->max)
/*TODO*///	      return 0;
/*TODO*///	
/*TODO*///	   if( (value < option->min) || (value > option->max) )
/*TODO*///	      return -1;
/*TODO*///	
/*TODO*///	   return 0;
/*TODO*///	}
/*TODO*///	
/*TODO*///	static int rc_set_defaults(struct rc_option *option)
/*TODO*///	{
/*TODO*///	   int i;
/*TODO*///	
/*TODO*///	   /* set the defaults */
/*TODO*///	   for(i=0; option[i].type; i++)
/*TODO*///	   {
/*TODO*///	      if (option[i].type == rc_link)
/*TODO*///	      {
/*TODO*///	         if(rc_set_defaults(option[i].dest))
/*TODO*///	            return -1;
/*TODO*///	      }
/*TODO*///	      else if (option[i].deflt && rc_set_option3(option+i, option[i].deflt,
/*TODO*///	         option[i].priority))
/*TODO*///	         return -1;
/*TODO*///	   }
/*TODO*///	
/*TODO*///	   return 0;
/*TODO*///	}
/*TODO*///	
/*TODO*///	static void rc_free_stuff(struct rc_option *option)
/*TODO*///	{
/*TODO*///	   int i;
/*TODO*///	
/*TODO*///	   for(i=0; option[i].type; i++)
/*TODO*///	   {
/*TODO*///	      switch (option[i].type)
/*TODO*///	      {
/*TODO*///	         case rc_link:
/*TODO*///	            rc_free_stuff(option[i].dest);
/*TODO*///	            break;
/*TODO*///	         case rc_string:
/*TODO*///	            if(*(char **)option[i].dest)
/*TODO*///	               free(*(char **)option[i].dest);
/*TODO*///	            break;
/*TODO*///	         case rc_file:
/*TODO*///	            if(*(FILE **)option[i].dest)
/*TODO*///	               fclose(*(FILE **)option[i].dest);
/*TODO*///	            break;
/*TODO*///	      }
/*TODO*///	   }
/*TODO*///	}

	/* public methods (in rc.h) */
	public static rc_struct rc_create()
	{
	   rc_struct rc = null;
	
	   if((rc = new rc_struct()) == null)
	   {
	      fprintf(System.out, "error: malloc failed for: struct rc_struct\n");
	      return null;
	   }
	
	   return rc;
	}
	
/*TODO*///	void rc_destroy(struct rc_struct *rc)
/*TODO*///	{
/*TODO*///	   if(rc->option)
/*TODO*///	   {
/*TODO*///	      rc_free_stuff(rc->option);
/*TODO*///	      free (rc->option);
/*TODO*///	   }
/*TODO*///	   if(rc->arg)
/*TODO*///	      free(rc->arg);
/*TODO*///	   free(rc);
/*TODO*///	}
	
	public static int rc_register(rc_struct rc, rc_option option)
	{
	   int i;
/*TODO*///	
/*TODO*///	   /* try to find a free entry in our option list */
/*TODO*///	   for(i = 0; i < rc->option_size; i++)
/*TODO*///	      if(rc->option[i].type <= 0)
/*TODO*///	         break;
/*TODO*///	
/*TODO*///	   /* do we have space to register this option list ? */
/*TODO*///	   if(i >= (rc->option_size-1))
/*TODO*///	   {
/*TODO*///	      struct rc_option *tmp = realloc(rc->option,
/*TODO*///	         (rc->option_size + BUF_SIZE) * sizeof(struct rc_option));
/*TODO*///	      if (tmp == 0)
/*TODO*///	      {
/*TODO*///	         fprintf(stderr, "error: malloc failed in rc_register_option\n");
/*TODO*///	         return -1;
/*TODO*///	      }
/*TODO*///	      rc->option = tmp;
/*TODO*///	      memset(rc->option + rc->option_size, 0, BUF_SIZE *
/*TODO*///	         sizeof(struct rc_option));
/*TODO*///	      rc->option_size += BUF_SIZE;
/*TODO*///	   }
/*TODO*///	
/*TODO*///	   /* set the defaults */
/*TODO*///	   if(rc_set_defaults(option))
/*TODO*///	      return -1;
/*TODO*///	
/*TODO*///	   /* register the option */
/*TODO*///	   rc->option[i].type = rc_link;
/*TODO*///	   rc->option[i].dest = option;
	
	   return 0;
	}
	
/*TODO*///	int rc_unregister(struct rc_struct *rc, struct rc_option *option)
/*TODO*///	{
/*TODO*///	   int i;
/*TODO*///	
/*TODO*///	   /* try to find the entry in our option list, unregister later registered
/*TODO*///	      duplicates first */
/*TODO*///	   for(i = rc->option_size - 1; i >= 0; i--)
/*TODO*///	   {
/*TODO*///	      if(rc->option[i].dest == option)
/*TODO*///	      {
/*TODO*///	         memset(rc->option + i, 0, sizeof(struct rc_option));
/*TODO*///	         rc->option[i].type = rc_ignore;
/*TODO*///	         return 0;
/*TODO*///	      }
/*TODO*///	   }
/*TODO*///	
/*TODO*///	   return -1;
/*TODO*///	}
/*TODO*///	
/*TODO*///	int rc_load(struct rc_struct *rc, const char *name,
/*TODO*///	   int priority, int continue_on_errors)
/*TODO*///	{
/*TODO*///	   FILE *f;
/*TODO*///	
/*TODO*///	   fprintf(stderr, "info: trying to parse: %s\n", name);
/*TODO*///	
/*TODO*///	   if (!(f = fopen(name, "r")))
/*TODO*///	      return 0;
/*TODO*///	
/*TODO*///	   return rc_read(rc, f, name, priority, continue_on_errors);
/*TODO*///	}
/*TODO*///	
/*TODO*///	int rc_save(struct rc_struct *rc, const char *name, int append)
/*TODO*///	{
/*TODO*///	   FILE *f;
/*TODO*///	
/*TODO*///	   if (!(f = fopen(name, append? "a":"w")))
/*TODO*///	      return -1;
/*TODO*///	
/*TODO*///	   return rc_write(rc, f, name);
/*TODO*///	}
/*TODO*///	
/*TODO*///	int osd_rc_read(struct rc_struct *rc, mame_file *f, const char *description,
/*TODO*///	   int priority, int continue_on_errors)
/*TODO*///	{
/*TODO*///	   char buf[BUF_SIZE];
/*TODO*///	   int line = 0;
/*TODO*///	
/*TODO*///	   while(mame_fgets(buf, BUF_SIZE, f))
/*TODO*///	   {
/*TODO*///	      struct rc_option *option;
/*TODO*///	      char *name, *tmp, *arg = NULL;
/*TODO*///	
/*TODO*///	      line ++;
/*TODO*///	
/*TODO*///	      /* get option name */
/*TODO*///	      if(!(name = strtok(buf, " \t\r\n")))
/*TODO*///	         continue;
/*TODO*///	      if(name[0] == '#')
/*TODO*///	         continue;
/*TODO*///	
/*TODO*///	      /* get complete rest of line */
/*TODO*///	      arg = strtok(NULL, "\r\n");
/*TODO*///	
/*TODO*///	      /* ignore white space */
/*TODO*///	      for (; (*arg == '\t' || *arg == ' '); arg++) {}
/*TODO*///	
/*TODO*///	      /* deal with quotations */
/*TODO*///	      if (arg[0] == '"')
/*TODO*///	         arg = strtok (arg, "\"");
/*TODO*///	      else if (arg[0] == '\'')
/*TODO*///	         arg = strtok (arg, "'");
/*TODO*///	      else
/*TODO*///	         arg = strtok (arg, " \t\r\n");
/*TODO*///	
/*TODO*///	      if(!(option = rc_get_option2(rc->option, name)))
/*TODO*///	      {
/*TODO*///	         fprintf(stderr, "error: unknown option %s, on line %d of file: %s\n",
/*TODO*///	            name, line, description);
/*TODO*///	      }
/*TODO*///	      else if (rc_requires_arg[option->type] && !arg)
/*TODO*///	      {
/*TODO*///	         fprintf(stderr,
/*TODO*///	            "error: %s requires an argument, on line %d of file: %s\n",
/*TODO*///	            name, line, description);
/*TODO*///	      }
/*TODO*///	      else if ( (tmp = strtok(NULL, " \t\r\n")) && (tmp[0] != '#') )
/*TODO*///	      {
/*TODO*///	         fprintf(stderr,
/*TODO*///	            "error: trailing garbage: \"%s\" on line: %d of file: %s\n",
/*TODO*///	            tmp, line, description);
/*TODO*///	      }
/*TODO*///	      else if (!rc_set_option3(option, arg, priority))
/*TODO*///	         continue;
/*TODO*///	
/*TODO*///	      if (continue_on_errors)
/*TODO*///	         fprintf(stderr, "   ignoring line\n");
/*TODO*///	      else
/*TODO*///	         return -1;
/*TODO*///	   }
/*TODO*///	   return 0;
/*TODO*///	}
/*TODO*///	
/*TODO*///	int rc_read(struct rc_struct *rc, FILE *f, const char *description,
/*TODO*///	   int priority, int continue_on_errors)
/*TODO*///	{
/*TODO*///	   char buf[BUF_SIZE];
/*TODO*///	   int line = 0;
/*TODO*///	
/*TODO*///	   while(fgets(buf, BUF_SIZE, f))
/*TODO*///	   {
/*TODO*///	      struct rc_option *option;
/*TODO*///	      char *name, *tmp, *arg = NULL;
/*TODO*///	
/*TODO*///	      line ++;
/*TODO*///	
/*TODO*///	      /* get option name */
/*TODO*///	      if(!(name = strtok(buf, " \t\r\n")))
/*TODO*///	         continue;
/*TODO*///	      if(name[0] == '#')
/*TODO*///	         continue;
/*TODO*///	
/*TODO*///	      /* get complete rest of line */
/*TODO*///	      arg = strtok(NULL, "\r\n");
/*TODO*///		  if (arg == 0)
/*TODO*///		  {
/*TODO*///			  fprintf(stderr, "error: garbage \"%s\" on line %d of file: %s\n",
/*TODO*///				  buf, line, description);
/*TODO*///			  continue;
/*TODO*///		  }
/*TODO*///	
/*TODO*///	      /* ignore white space */
/*TODO*///	      for (; (*arg == '\t' || *arg == ' '); arg++) {}
/*TODO*///	
/*TODO*///	      /* deal with quotations */
/*TODO*///	      if (arg[0] == '"')
/*TODO*///	         arg = strtok (arg, "\"");
/*TODO*///	      else if (arg[0] == '\'')
/*TODO*///	         arg = strtok (arg, "'");
/*TODO*///	      else
/*TODO*///	         arg = strtok (arg, " \t\r\n");
/*TODO*///	
/*TODO*///	      if(!(option = rc_get_option2(rc->option, name)))
/*TODO*///	      {
/*TODO*///	         fprintf(stderr, "error: unknown option %s, on line %d of file: %s\n",
/*TODO*///	            name, line, description);
/*TODO*///	      }
/*TODO*///	      else if (rc_requires_arg[option->type] && !arg)
/*TODO*///	      {
/*TODO*///	         fprintf(stderr,
/*TODO*///	            "error: %s requires an argument, on line %d of file: %s\n",
/*TODO*///	            name, line, description);
/*TODO*///	      }
/*TODO*///	      else if ( (tmp = strtok(NULL, " \t\r\n")) && (tmp[0] != '#') )
/*TODO*///	      {
/*TODO*///	         fprintf(stderr,
/*TODO*///	            "error: trailing garbage: \"%s\" on line: %d of file: %s\n",
/*TODO*///	            tmp, line, description);
/*TODO*///	      }
/*TODO*///	      else if (!rc_set_option3(option, arg, priority))
/*TODO*///	         continue;
/*TODO*///	
/*TODO*///	      if (continue_on_errors)
/*TODO*///	         fprintf(stderr, "   ignoring line\n");
/*TODO*///	      else
/*TODO*///	         return -1;
/*TODO*///	   }
/*TODO*///	   return 0;
/*TODO*///	}
/*TODO*///	
/*TODO*///	/* needed to walk the tree */
/*TODO*///	static int rc_real_write(struct rc_option *option, FILE *f,
/*TODO*///	   const char *description)
/*TODO*///	{
/*TODO*///	   int i;
/*TODO*///	
/*TODO*///	   if (description)
/*TODO*///	      fprintf(f, "### %s ###\n", description);
/*TODO*///	
/*TODO*///	   for(i=0; option[i].type; i++)
/*TODO*///	   {
/*TODO*///	      switch (option[i].type)
/*TODO*///	      {
/*TODO*///	         case rc_seperator:
/*TODO*///	            fprintf(f, "\n### %s ###\n", option[i].name);
/*TODO*///	            break;
/*TODO*///	         case rc_link:
/*TODO*///	            if(rc_real_write(option[i].dest, f, NULL))
/*TODO*///	               return -1;
/*TODO*///	            break;
/*TODO*///	         case rc_string:
/*TODO*///	            if(!*(char **)option[i].dest)
/*TODO*///	            {
/*TODO*///	               fprintf(f, "# %-19s   <NULL> (not set)\n", option[i].name);
/*TODO*///	               break;
/*TODO*///	            }
/*TODO*///	         case rc_bool:
/*TODO*///	         case rc_int:
/*TODO*///	         case rc_float:
/*TODO*///	            fprintf(f, "%-21s   ", option[i].name);
/*TODO*///	            switch(option[i].type)
/*TODO*///	            {
/*TODO*///	               case rc_bool:
/*TODO*///	               case rc_int:
/*TODO*///	                  fprintf(f, "%d\n", *(int *)option[i].dest);
/*TODO*///	                  break;
/*TODO*///	               case rc_float:
/*TODO*///	                  fprintf(f, "%f\n", *(float *)option[i].dest);
/*TODO*///	                  break;
/*TODO*///	               case rc_string:
/*TODO*///	                  fprintf(f, "%s\n", *(char **)option[i].dest);
/*TODO*///	                  break;
/*TODO*///	            }
/*TODO*///	            break;
/*TODO*///	      }
/*TODO*///	   }
/*TODO*///	   if (description)
/*TODO*///	      fprintf(f, "\n");
/*TODO*///	   return 0;
/*TODO*///	}
/*TODO*///	
/*TODO*///	int rc_write(struct rc_struct *rc, FILE *f, const char *description)
/*TODO*///	{
/*TODO*///	   return rc_real_write(rc->option, f, description);
/*TODO*///	}
	
	public static int rc_parse_commandline(rc_struct rc, int argc, String[] argv,
	   int priority, arg_callbackPtr arg_callback)
	{
	   int i;
	
	   for(i=1; i<argc; i++)
	   {
	      if(argv[i].charAt(0) == '-')
	      {
/*TODO*///	         int start = 1;
/*TODO*///	         struct rc_option *option;
/*TODO*///	         String arg = null;
/*TODO*///	
/*TODO*///	         if(argv[i][1] == '-')
/*TODO*///	            start = 2;
/*TODO*///	
/*TODO*///	         if((option = rc_get_option2(rc->option, argv[i] + start)))
/*TODO*///	         {
/*TODO*///	            if (option->type == rc_bool)
/*TODO*///	            {
/*TODO*///	               /* handle special bool set case */
/*TODO*///	               arg = "1";
/*TODO*///	            }
/*TODO*///	            else
/*TODO*///	            {
/*TODO*///	               /* normal option */
/*TODO*///	               if (rc_requires_arg[option->type])
/*TODO*///	               {
/*TODO*///	                  i++;
/*TODO*///	                  if (i >= argc)
/*TODO*///	                  {
/*TODO*///	                     fprintf(stderr, "error: %s requires an argument\n", argv[i-1]);
/*TODO*///	                     return -1;
/*TODO*///	                  }
/*TODO*///	                  arg = argv[i];
/*TODO*///	               }
/*TODO*///	            }
/*TODO*///	         }
/*TODO*///	         else if(!strncmp(argv[i] + start, "no", 2) &&
/*TODO*///	            (option = rc_get_option2(rc->option, argv[i] + start + 2)) &&
/*TODO*///	            (option->type == rc_bool))
/*TODO*///	         {
/*TODO*///	            /* handle special bool clear case */
/*TODO*///	            arg = "0";
/*TODO*///	         }
/*TODO*///	         else
/*TODO*///	         {
/*TODO*///	            fprintf(stderr, "error: unknown option %s\n", argv[i]);
/*TODO*///	            return -1;
/*TODO*///	         }
/*TODO*///	
/*TODO*///	         if(rc_set_option3(option, arg, priority))
/*TODO*///	            return -1;
	      }
	      else
	      {
/*TODO*///	         /* do we have space to register the non-option arg */
/*TODO*///	         if(rc->args_registered >= (rc->arg_size))
/*TODO*///	         {
/*TODO*///	            char **tmp = realloc(rc->arg, (rc->arg_size + BUF_SIZE) *
/*TODO*///	               sizeof(char *));
/*TODO*///	            if (tmp == 0)
/*TODO*///	            {
/*TODO*///	               fprintf(stderr,
/*TODO*///	                  "error: malloc failed in rc_parse_commadline\n");
/*TODO*///	               return -1;
/*TODO*///	            }
/*TODO*///	            rc->arg = tmp;
/*TODO*///	            memset(rc->arg + rc->arg_size, 0, BUF_SIZE * sizeof(char *));
/*TODO*///	            rc->arg_size += BUF_SIZE;
/*TODO*///	         }
/*TODO*///	
/*TODO*///	         /* register the non-option arg */
/*TODO*///	         rc->arg[rc->args_registered] = argv[i];
/*TODO*///	         rc->args_registered++;
	
	         /* call the callback if defined */
	         if(arg_callback!=null && (arg_callback).handler(argv[i])!=0)
	            return -1;
	      }
	   }
	   return 0;
	}
	
/*TODO*///	int rc_get_non_option_args(struct rc_struct *rc, int *argc, char **argv[])
/*TODO*///	{
/*TODO*///	   *argv = rc->arg;
/*TODO*///	   *argc = rc->args_registered;
/*TODO*///	   return 0;
/*TODO*///	}
/*TODO*///	
/*TODO*///	/* needed to walk the tree */
/*TODO*///	static void rc_real_print_help(struct rc_option *option, FILE *f)
/*TODO*///	{
/*TODO*///	   int i;
/*TODO*///	   char buf[BUF_SIZE];
/*TODO*///	   static const char *type_name[] = {"", "", " <string>", " <int>", " <float>",
/*TODO*///	      "", "", " <filename>", " <arg>", "", "" };
/*TODO*///	
/*TODO*///	   for(i=0; option[i].type; i++)
/*TODO*///	   {
/*TODO*///	      switch (option[i].type)
/*TODO*///	      {
/*TODO*///	         case rc_ignore:
/*TODO*///	            break;
/*TODO*///	         case rc_seperator:
/*TODO*///	            fprintf(f, "\n*** %s ***\n", option[i].name);
/*TODO*///	            break;
/*TODO*///	         case rc_link:
/*TODO*///	            rc_real_print_help(option[i].dest, f);
/*TODO*///	            break;
/*TODO*///	         default:
/*TODO*///	            snprintf(buf, BUF_SIZE, "-%s%s%s%s%s%s",
/*TODO*///	               (option[i].type == rc_bool)? "[no]":"",
/*TODO*///	               option[i].name,
/*TODO*///	               (option[i].shortname)? " / -":"",
/*TODO*///	               (option[i].shortname && (option[i].type == rc_bool))? "[no]":"",
/*TODO*///	               (option[i].shortname)? option[i].shortname:"",
/*TODO*///	               type_name[option[i].type]);
/*TODO*///	            fprint_colums(f, buf,
/*TODO*///	               (option[i].help)? option[i].help:"no help available");
/*TODO*///	      }
/*TODO*///	   }
/*TODO*///	}
/*TODO*///	
/*TODO*///	void rc_print_help(struct rc_struct *rc, FILE *f)
/*TODO*///	{
/*TODO*///	   rc_real_print_help(rc->option, f);
/*TODO*///	}
/*TODO*///	
/*TODO*///	/* needed to walk the tree */
/*TODO*///	static void rc_real_print_man_options(struct rc_option *option, FILE *f)
/*TODO*///	{
/*TODO*///	   int i;
/*TODO*///	   static const char *type_name[] = {"", "", " Ar string", " Ar int",
/*TODO*///	      " Ar float", "", "", " Ar filename", " Ar arg", "", "" };
/*TODO*///	
/*TODO*///	   for(i=0; option[i].type; i++)
/*TODO*///	   {
/*TODO*///	      switch (option[i].type)
/*TODO*///	      {
/*TODO*///	         case rc_ignore:
/*TODO*///	            break;
/*TODO*///	         case rc_seperator:
/*TODO*///	            fprintf(f, ".It \\fB*** %s ***\\fR\n", option[i].name);
/*TODO*///	            break;
/*TODO*///	         case rc_link:
/*TODO*///	            rc_real_print_man_options(option[i].dest, f);
/*TODO*///	            break;
/*TODO*///	         default:
/*TODO*///	            fprintf(f, ".It Fl %s%s%s%s%s%s\n%s\n",
/*TODO*///	               (option[i].type == rc_bool)? "[no]":"",
/*TODO*///	               option[i].name,
/*TODO*///	               (option[i].shortname)? " , ":"",
/*TODO*///	               (option[i].shortname && (option[i].type == rc_bool))? "[no]":"",
/*TODO*///	               (option[i].shortname)? option[i].shortname:"",
/*TODO*///	               type_name[option[i].type],
/*TODO*///	               (option[i].help)? option[i].help:"no help available");
/*TODO*///	      }
/*TODO*///	   }
/*TODO*///	}
/*TODO*///	
/*TODO*///	void rc_print_man_options(struct rc_struct *rc, FILE *f)
/*TODO*///	{
/*TODO*///	   rc_real_print_man_options(rc->option, f);
/*TODO*///	}
/*TODO*///	
/*TODO*///	int rc_verify_power_of_2(struct rc_option *option, const char *arg,
/*TODO*///	   int priority)
/*TODO*///	{
/*TODO*///	   int i, value;
/*TODO*///	
/*TODO*///	   value = *(int *)option->dest;
/*TODO*///	
/*TODO*///	   for(i=0; i<(sizeof(int)*8); i++)
/*TODO*///	      if(((int)0x01 << i) == value)
/*TODO*///	         break;
/*TODO*///	   if(i == (sizeof(int)*8))
/*TODO*///	   {
/*TODO*///	      fprintf(stderr, "error invalid value for %s: %s\n", option->name, arg);
/*TODO*///	      return -1;
/*TODO*///	   }
/*TODO*///	
/*TODO*///	   option->priority = priority;
/*TODO*///	
/*TODO*///	   return 0;
/*TODO*///	}
/*TODO*///	
/*TODO*///	int rc_option_requires_arg(struct rc_struct *rc, const char *name)
/*TODO*///	{
/*TODO*///	   return rc_option_requires_arg2(rc->option, name);
/*TODO*///	}
/*TODO*///	
/*TODO*///	int rc_option_requires_arg2(struct rc_option *option, const char *name)
/*TODO*///	{
/*TODO*///	   struct rc_option *my_option;
/*TODO*///	
/*TODO*///	   if(!(my_option = rc_get_option2(option, name)))
/*TODO*///	   {
/*TODO*///	      fprintf(stderr, "error: unknown option %s\n", name);
/*TODO*///	      return -1;
/*TODO*///	   }
/*TODO*///	   return rc_requires_arg[my_option->type];
/*TODO*///	}
/*TODO*///	
/*TODO*///	int rc_option_requires_arg3(struct rc_option *option)
/*TODO*///	{
/*TODO*///	   return rc_requires_arg[option->type];
/*TODO*///	}
/*TODO*///	
/*TODO*///	int rc_get_priority(struct rc_struct *rc, const char *name)
/*TODO*///	{
/*TODO*///	   return rc_get_priority2(rc->option, name);
/*TODO*///	}
/*TODO*///	
/*TODO*///	int rc_get_priority2(struct rc_option *option, const char *name)
/*TODO*///	{
/*TODO*///	   struct rc_option *my_option;
/*TODO*///	
/*TODO*///	   if(!(my_option = rc_get_option2(option, name)))
/*TODO*///	   {
/*TODO*///	      fprintf(stderr, "error: unknown option %s\n", name);
/*TODO*///	      return -1;
/*TODO*///	   }
/*TODO*///	   return my_option->priority;
/*TODO*///	}
/*TODO*///	
/*TODO*///	int rc_get_priority3(struct rc_option *option)
/*TODO*///	{
/*TODO*///	   return option->priority;
/*TODO*///	}
/*TODO*///	
/*TODO*///	int rc_set_option(struct rc_struct *rc, const char *name, const char *arg,
/*TODO*///	   int priority)
/*TODO*///	{
/*TODO*///	   return rc_set_option2(rc->option, name, arg, priority);
/*TODO*///	}
/*TODO*///	
/*TODO*///	int rc_set_option2(struct rc_option *option, const char *name,
/*TODO*///	   const char *arg, int priority)
/*TODO*///	{
/*TODO*///	   struct rc_option *my_option;
/*TODO*///	
/*TODO*///	   if(!(my_option = rc_get_option2(option, name)))
/*TODO*///	   {
/*TODO*///	      fprintf(stderr, "error: unknown option %s\n", name);
/*TODO*///	      return -1;
/*TODO*///	   }
/*TODO*///	   return rc_set_option3(my_option, arg, priority);
/*TODO*///	}
/*TODO*///	
/*TODO*///	int rc_set_option3(struct rc_option *option, const char *arg, int priority)
/*TODO*///	{
/*TODO*///	   char *end;
/*TODO*///	
/*TODO*///	   /* check priority */
/*TODO*///	   if(priority < option->priority)
/*TODO*///	      return 0;
/*TODO*///	
/*TODO*///	   switch(option->type)
/*TODO*///	   {
/*TODO*///	      case rc_string:
/*TODO*///	         {
/*TODO*///	            char *str;
/*TODO*///	            if ( !( str = malloc(strlen(arg)+1) ) )
/*TODO*///	            {
/*TODO*///	               fprintf(stderr, "error: malloc failed for %s\n", option->name);
/*TODO*///	               return -1;
/*TODO*///	            }
/*TODO*///	            strcpy(str, arg);
/*TODO*///	            if(*(char **)option->dest)
/*TODO*///	               free(*(char **)option->dest);
/*TODO*///	            *(char **)option->dest = str;
/*TODO*///	         }
/*TODO*///	         break;
/*TODO*///	      case rc_int:
/*TODO*///	      case rc_bool:
/*TODO*///	         {
/*TODO*///	            int x;
/*TODO*///	            x = strtol(arg, &end, 0);
/*TODO*///	            if (*end || rc_verify(option, x))
/*TODO*///	            {
/*TODO*///	               fprintf(stderr, "error invalid value for %s: %s\n", option->name, arg);
/*TODO*///	               return -1;
/*TODO*///	            }
/*TODO*///	            *(int *)option->dest = x;
/*TODO*///	         }
/*TODO*///	         break;
/*TODO*///	      case rc_float:
/*TODO*///	         {
/*TODO*///	            float x;
/*TODO*///	            x = strtod(arg, &end);
/*TODO*///	            if (*end || rc_verify(option, x))
/*TODO*///	            {
/*TODO*///	               fprintf(stderr, "error invalid value for %s: %s\n", option->name, arg);
/*TODO*///	               return -1;
/*TODO*///	            }
/*TODO*///	            *(float *)option->dest = x;
/*TODO*///	         }
/*TODO*///	         break;
/*TODO*///	      case rc_set_int:
/*TODO*///	         *(int*)option->dest = option->min;
/*TODO*///	         break;
/*TODO*///	      case rc_file:
/*TODO*///	         {
/*TODO*///	            FILE *f = fopen(arg, (option->min)? "w":"r");
/*TODO*///	            if (f == 0)
/*TODO*///	            {
/*TODO*///	               fprintf(stderr, "error: couldn't open file: %s\n", arg);
/*TODO*///	               return -1;
/*TODO*///	            }
/*TODO*///	            if (*(FILE **)option->dest)
/*TODO*///	               fclose(*(FILE **)option->dest);
/*TODO*///	            *(FILE **)option->dest = f;
/*TODO*///	         }
/*TODO*///	         break;
/*TODO*///	      case rc_use_function:
/*TODO*///	      case rc_use_function_no_arg:
/*TODO*///	         break;
/*TODO*///	      default:
/*TODO*///	         fprintf(stderr,
/*TODO*///	            "error: unknown option type: %d, this should not happen!\n",
/*TODO*///	            option->type);
/*TODO*///	         return -1;
/*TODO*///	   }
/*TODO*///	   /* functions should do there own priority handling, so that they can
/*TODO*///	      ignore priority handling if they wish */
/*TODO*///	   if(option->func)
/*TODO*///	      return (*option->func)(option, arg, priority);
/*TODO*///	
/*TODO*///	   option->priority = priority;
/*TODO*///	
/*TODO*///	   return 0;
/*TODO*///	}
/*TODO*///	
/*TODO*///	struct rc_option *rc_get_option(struct rc_struct *rc, const char *name)
/*TODO*///	{
/*TODO*///	   return rc_get_option2(rc->option, name);
/*TODO*///	}
/*TODO*///	
/*TODO*///	struct rc_option *rc_get_option2(struct rc_option *option, const char *name)
/*TODO*///	{
/*TODO*///	   int i;
/*TODO*///	   struct rc_option *result;
/*TODO*///	
/*TODO*///	   for(i=0; option[i].type; i++)
/*TODO*///	   {
/*TODO*///	      switch(option[i].type)
/*TODO*///	      {
/*TODO*///	         case rc_ignore:
/*TODO*///	         case rc_seperator:
/*TODO*///	            break;
/*TODO*///	         case rc_link:
/*TODO*///	            if((result = rc_get_option2(option[i].dest, name)))
/*TODO*///	               return result;
/*TODO*///	            break;
/*TODO*///	         default:
/*TODO*///	            if(!strcmp(name, option[i].name) ||
/*TODO*///	               (option[i].shortname &&
/*TODO*///	                  !strcmp(name, option[i].shortname)))
/*TODO*///	               return &option[i];
/*TODO*///	      }
/*TODO*///	   }
/*TODO*///	   return NULL;
/*TODO*///	}
/*TODO*///	
/*TODO*///	/* gimmi the entire tree, I want todo all the parsing myself */
/*TODO*///	struct rc_option *rc_get_options(struct rc_struct *rc)
/*TODO*///	{
/*TODO*///	   return rc->option;
/*TODO*///	}
/*TODO*///	
/*TODO*///	
/*TODO*///	
/*TODO*///	#if 0
/*TODO*///	/* various utility methods which don't really belong to the rc object,
/*TODO*///	   but seem to fit here well */
/*TODO*///	
/*TODO*///	/* locate user's home directory */
/*TODO*///	char *rc_get_home_dir(void)
/*TODO*///	{
/*TODO*///	   struct passwd *pw;
/*TODO*///	   char *s;
/*TODO*///	
/*TODO*///	   if (!(pw=getpwuid(getuid())))
/*TODO*///	   {
/*TODO*///	      fprintf(stderr, "Who are you? Not found in passwd database!!\n");
/*TODO*///	      return NULL;
/*TODO*///	   }
/*TODO*///	   if (!(s=malloc(strlen(pw->pw_dir)+1)))
/*TODO*///	   {
/*TODO*///	      fprintf(stderr, "error: malloc faild for homedir string\n");
/*TODO*///	      return NULL;
/*TODO*///	   }
/*TODO*///	   strcpy(s, pw->pw_dir);
/*TODO*///	   return s;
/*TODO*///	}
/*TODO*///	
/*TODO*///	/*
/*TODO*///	 * check and if nescesarry create dir
/*TODO*///	 */
/*TODO*///	int rc_check_and_create_dir(const char *name)
/*TODO*///	{
/*TODO*///	   struct stat stat_buffer;
/*TODO*///	
/*TODO*///	   if (stat(name, &stat_buffer))
/*TODO*///	   {
/*TODO*///	      /* error check if it doesn't exist or something else is wrong */
/*TODO*///	      if (errno == ENOENT)
/*TODO*///	      {
/*TODO*///	         /* doesn't exist letts create it ;) */
/*TODO*///	#ifdef BSD43
/*TODO*///		 if (mkdir(name, 0775))
/*TODO*///	#else
/*TODO*///	         if (mkdir(name, S_IRWXU|S_IRWXG|S_IROTH|S_IXOTH))
/*TODO*///	#endif
/*TODO*///	         {
/*TODO*///	            fprintf(stderr, "Error creating dir %s", name);
/*TODO*///	            perror(" ");
/*TODO*///	            return -1;
/*TODO*///	         }
/*TODO*///	      }
/*TODO*///	      else
/*TODO*///	      {
/*TODO*///	         /* something else went wrong yell about it */
/*TODO*///	         fprintf(stderr, "Error opening %s", name);
/*TODO*///	         perror(" ");
/*TODO*///	         return -1;
/*TODO*///	      }
/*TODO*///	   }
/*TODO*///	   else
/*TODO*///	   {
/*TODO*///	      /* file exists check it's a dir otherwise yell about it */
/*TODO*///	#ifdef BSD43
/*TODO*///	      if(!(S_IFDIR & stat_buffer.st_mode))
/*TODO*///	#else
/*TODO*///	      if(!S_ISDIR(stat_buffer.st_mode))
/*TODO*///	#endif
/*TODO*///	      {
/*TODO*///	         fprintf(stderr,"Error %s exists but isn't a dir\n", name);
/*TODO*///	         return -1;
/*TODO*///	      }
/*TODO*///	   }
/*TODO*///	   return 0;
/*TODO*///	}
/*TODO*///	#endif
}
