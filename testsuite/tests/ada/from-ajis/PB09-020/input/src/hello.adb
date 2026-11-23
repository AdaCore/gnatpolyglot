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

package body Hello is

   --------
   -- F2 --
   --------

   function F2 (This : Editor_Buffer) return Group_Block is
   begin
      return G : Group_Block do null; end return;
   end F2;

   --------
   -- F3 --
   --------

   function F3 (This : Editor_Buffer) return Group_Block is
   begin
      return G : Group_Block do Commands.Set (G, 10); end return;
   end F3;

   --------
   -- F4 --
   --------

   function F4 (This : T1) return Integer is
   begin
      return Get (This.F5);
   end F4;

   --------
   -- F5 --
   --------

   function F5 (This : T1) return Group_Block is
   begin
      return G : Group_Block do Commands.Set (G, 5); end return;
   end F5;

   --------
   -- F6 --
   --------

   function F6 (This : T1) return Group_Block is
   begin
      return G : Group_Block do Commands.Set (G, 6); end return;
   end F6;

   --------
   -- F7 --
   --------

   function F7 (This : T2) return Integer is
   begin
      return Get (This.F9);
   end F7;

   --------
   -- F8 --
   --------

   function F8 (This : T2) return Group_Block is
   begin
      return G : Group_Block do Commands.Set (G, 8); end return;
   end F8;

   --------
   -- F9 --
   --------

   function F9 (This : T2) return Group_Block is
   begin
      return G : Group_Block do Commands.Set (G, 9); end return;
   end F9;

end Hello;
