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

package body Test is

   procedure Say (Something : String) is
   begin
      Put_Line ("Ada says: " & Something);
   end Say;

   task body T_0 is
   begin
      null;
   end T_0;

   task body T_1 is
   begin
      accept E do
         null;
      end E;
   end T_1;

   task body T_2 is
   begin
      accept E do
         null;
      end E;
   end T_2;

   protected body T_3 is
      entry E when True is
      begin
         null;
      end E;
   end T_3;

end Test;
