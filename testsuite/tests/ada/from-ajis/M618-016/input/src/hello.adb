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

with Ada.Text_IO; use Ada.Text_IO;

package body Hello is

   function F1 return T is
      R : T;
   begin
      return R;
   end F1;

   function F1 return Integer is
   begin
      return 0;
   end F1;

   function F2 return T'Class is
      R : T;
   begin
      return R;
   end F2;

   function F2 return Integer is
   begin
      return 0;
   end F2;

   function F3 (T1 : Integer) return T is
      R : T;
   begin
      return R;
   end F3;

   function F3 (T2 : Integer) return T'Class is
      R : T;
   begin
      return R;
   end F3;

   function F3 (T2 : Integer) return Integer is
   begin
      return 0;
   end F3;

   function F4 return T is
      R : T;
   begin
      return R;
   end F4;

   function F4 return T'Class is
      R : T;
   begin
      return R;
   end F4;

   function F5 return T is
      R : T;
   begin
      return R;
   end F5;

   function F5 return T'Class is
      R : T;
   begin
      return R;
   end F5;

   procedure Say (Something : String) is
   begin
      Put_Line ("Ada says: " & Something);
   end Say;

end Hello;
