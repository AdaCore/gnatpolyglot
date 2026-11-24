------------------------------------------------------------------------------
--                                                                          --
--                     Copyright (C) 2017, AdaCore                          --
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

package body Hello is

   procedure Test
     (P1 : Ada_Record;
      P2 : Ada_Record'Class;
      P3 : Ada_Record_Ptr;
      P4 : Ada_Record_Class_Ptr;
      P5 : in out Ada_Record;
      P6 : in out Ada_Record'Class;
      P7 : in out Ada_Record_Ptr;
      P8 : in out Ada_Record_Class_Ptr;
      P9 : out Ada_Record;
      P10 : out Ada_Record'Class;
      P11 : out Ada_Record_Ptr;
      P12 : out Ada_Record_Class_Ptr
     ) is
   begin
      P1.Print;
      P2.Print;
      P3.Print;
      P4.Print;
      P5.Print;
      P6.Print;
      P7.Print;
      P8.Print;

      P12 := P8;
      P11 := P7;
      P10 := P6;
      P9 := P5;
      P8 := P4;
      P7 := P3;
      P6 := P2;
      P5 := P1;
   end Test;

   procedure Print (P : Ada_Record) is
   begin
      Put_Line ("Ada says: " & P.Value'Image);
   end Print;

end Hello;
