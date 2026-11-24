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

package Hello is

   type Ada_Record is tagged record
      Value : Integer;
   end record;

   type Ada_Record_Ptr is access all Ada_Record;
   type Ada_Record_Class_Ptr is access all Ada_Record'CLass;

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
     );

   procedure Print (P : Ada_Record);

end Hello;
