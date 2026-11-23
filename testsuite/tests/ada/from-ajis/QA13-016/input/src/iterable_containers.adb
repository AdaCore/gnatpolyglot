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

package body Iterable_Containers is

   function Iterate
     (This : Integer_Container)
      return Integer_Iterators.Iterator'Class
   is
   begin
      return Integer_Iterator'(Integer_Iterators.Iterator with
                                 Val => This.First, Last => This.Last);
   end Iterate;

   ---------------------------------------------------------------------------
   overriding
   procedure Next
     (This : not null access Integer_Iterator)
   is
   begin
      This.Val := This.Val + 1;
   end Next;

end Iterable_Containers;
