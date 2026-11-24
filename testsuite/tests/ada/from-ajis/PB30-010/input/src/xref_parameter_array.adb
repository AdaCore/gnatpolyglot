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

package body Xref_Parameter_Array is

   Params : Parameter_Array (1 .. 1) := ((1 => (Parameter => True)));

   ---------------------
   -- Should_Be_Bound --
   ---------------------

   procedure Should_Be_Bound (P1 : Boolean; P2 : access Root_Entity'Class) is
   begin
      null;
   end Should_Be_Bound;

   ----------------
   -- Parameters --
   ----------------

   overriding function Parameters
     (Entity : General_Entity)
      return Parameter_Array
   is
   begin
      return Params;
   end Parameters;

end Xref_Parameter_Array;
