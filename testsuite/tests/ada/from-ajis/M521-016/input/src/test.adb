------------------------------------------------------------------------------
--                                                                          --
--                     Copyright (C) 2013, AdaCore                          --
--                                                                          --
-- This is free software;  you can redistribute it  and/or modify it  under --
-- terms of the  GNU General Public License as published  by the Free Soft- --
-- ware  Foundation;  either version 3,  or (at your option) any later ver- --
-- sion.  This software is distributed in the hope  that it will be useful, --
-- but WITHOUT ANY WARRANTY;  without even the implied warranty of MERCHAN- --
-- TABILITY or FITNESS FOR A PARTICULAR PURPOSE. See the GNU General Public --
-- License for  more details.  You should have  received  a copy of the GNU --
-- General  Public  License  distributed  with  this  software;   see  file --
-- COPYING3.  If not, go to http://www.gnu.org/licenses for a complete copy --
-- of the license.                                                          --
------------------------------------------------------------------------------

with Ada.Exceptions;
with GNAT.Traceback.Symbolic;
with Interfaces.C.Strings;

package body Test is

   type Argv_Array is array (0 .. 0) of Interfaces.C.Strings.chars_ptr;
   my_gnat_argv : aliased Argv_Array;

   procedure Init_Gnat_Argv is
      gnat_argv : access Argv_Array;
      pragma Import (C, gnat_argv, "gnat_argv");
   begin
      if gnat_argv = null then
         my_gnat_argv (0) := Interfaces.C.Strings.New_String ("java");
         gnat_argv := my_gnat_argv'Access;
      end if;

   end Init_Gnat_Argv;

   function Test_Symbolic_Traceback_From_Java return String is
   begin
      begin
         raise Test_Exception with
            "Test_Exception from Ada in Test_Symbolic_Traceback_From_Java";
      exception
         when Te : Test_Exception =>
            return GNAT.Traceback.Symbolic.Symbolic_Traceback (Te);
      end;
      return "No exception on Symbolic_Traceback";
   end Test_Symbolic_Traceback_From_Java;

   function Test_Exception_Information_From_Java return String is
   begin
      begin
         Init_Gnat_Argv;
         raise Test_Exception with
            "Test_Exception from Ada in Test_Symbolic_Traceback_From_Java";
      exception
         when Te : Test_Exception =>
            return Ada.Exceptions.Exception_Information (Te);
      end;
      return "No exception on Exception_Information";
   end Test_Exception_Information_From_Java;

end Test;
