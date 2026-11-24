------------------------------------------------------------------------------
--                                 ADA2JAVA-TEST                            --
--                                                                          --
--                     Copyright (C) 2016, AdaCore                          --
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

with Ada.Text_IO; use Ada.Text_IO;

package body Test is

   function F_1 return A_Type'Class is
      V : A_Type;
   begin
      V.Should_Be_Freed := True;
      return V;
   end F_1;

   function F_2 return A_Type_Acc is
      V : A_Type_Acc := new A_Type;
   begin
      V.Should_Be_Freed := False;
      return V;
   end F_2;

   procedure Initialize (V : in out A_Type) is
   begin
      null;
   end Initialize;

   procedure Finalize (V : in out A_Type) is
   begin
      if Display_Finalize then
         if V.Should_Be_Freed then
            Put_Line ("EXPECTED FREE");
            Expected_Freed := Expected_Freed + 1;
         end if;
      end if;

      if not V.Should_Be_Freed then
         Put_Line ("UNEXPECTED FREE");
      end if;
   end Finalize;

   procedure Adjust (V : in out A_Type) is
   begin
      null;
   end Adjust;
end Test;
