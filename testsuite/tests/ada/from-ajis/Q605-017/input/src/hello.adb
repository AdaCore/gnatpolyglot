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

   ---------
   -- Say --
   ---------

   procedure Say (Self : Parent_Type) is
   begin
      Put_Line ("Ada Parent");
   end Say;

   ----------
   -- Proc --
   ----------

   procedure Proc (Self : Parent_Type; Object : in out Parent_Type_Access) is
   begin
      Say (Self);
      Say (Object.all);
   end Proc;

   ---------
   -- Say --
   ---------

   procedure Say (Self : Child_Type) is
   begin
      Put_Line ("Ada Child");
   end Say;

   ----------
   -- Proc --
   ----------

   procedure Proc (Self : Child_Type; Object : in out Child_Type_Access) is
   begin
      Put_Line ("Ada Child Proc");
      Say (Self);
      Say (Object.all);
   end Proc;

end Hello;
