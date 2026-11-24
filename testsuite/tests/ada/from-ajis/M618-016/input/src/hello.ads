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


package Hello is

   type T is tagged null record;

   function F1 return T;
   function F1 return Integer;

   function F2 return T'Class;
   function F2 return Integer;

   function F3 (T1 : Integer) return T;
   function F3 (T2 : Integer) return T'Class;
   function F3 (T2 : Integer) return Integer;

   function F4 return T;
   function F4 return T'Class;

   function F5 return T'Class;
   function F5 return T;

   procedure Say (Something : String);

end Hello;
