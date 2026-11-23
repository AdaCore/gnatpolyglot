------------------------------------------------------------------------------
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

with Ada.Finalization; use Ada.Finalization;
with Commands; use Commands;

package Hello is

   type Editor_Buffer is abstract new Controlled with null record;

   function F1 (This : Editor_Buffer) return Integer is abstract;

   function F2 (This : Editor_Buffer) return Group_Block;

   function F3 (This : Editor_Buffer) return Group_Block;

   type T1 is tagged null record;

   function F4 (This : T1) return Integer;

   function F5 (This : T1) return Group_Block;

   function F6 (This : T1) return Group_Block;

   type T2 is tagged null record;

   function F7 (This : T2) return Integer;

   function F8 (This : T2) return Group_Block;

   function F9 (This : T2) return Group_Block;

end Hello;
