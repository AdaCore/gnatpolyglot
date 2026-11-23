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

with Dispatching_Iterators_Generic;

package Iterable_Containers is

   package Integer_Iterators is new Dispatching_Iterators_Generic (Integer);

   type Integer_Container is tagged limited
      record
         First : Integer := 0;
         Last  : Integer := 10;
      end record;

   function Iterate
     (This : Integer_Container)
      return Integer_Iterators.Iterator'Class;

private

   type Integer_Iterator is new Integer_Iterators.Iterator with
      record
         Val  : Integer := 0;
         Last : Integer := 0;
      end record;

   overriding
   function Has_Element
     (This : Integer_Iterator)
      return Boolean
   is
     (This.Val <= This.Last);

   overriding
   function Current_Element
     (This : Integer_Iterator)
      return Integer
   is
     (This.Val);

   overriding
   procedure Next
     (This : not null access Integer_Iterator);

end Iterable_Containers;
