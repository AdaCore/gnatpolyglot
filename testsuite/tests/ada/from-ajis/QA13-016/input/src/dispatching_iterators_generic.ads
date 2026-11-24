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

generic
   type Element_Type is private;
package Dispatching_Iterators_Generic is

   type Iterator is abstract tagged limited private
     with
       Iterable => (First       => Get_First,
                    Next        => Next,
                    Has_Element => Has_Element,
                    Element     => Get_Element);
   --
   --  This type provides support for a dispatching iteration pattern.
   --  The intent of this is to support the the definition of iterators on
   --  interface types.
   --
   --  The standard approach to iteration in Ada (via both the Default_Iterator
   --  aspect and the GNAT Iterable aspect) does not readily support interfaces
   --  with iterators. The problem with the Ada iteration concept is that the
   --  Cursor type, which encapsulates the current position through the
   --  iteration, must be fully defined at the point where aspects are added to
   --  the type.
   --
   --  The abstract Iterator defined here shifts the encapsulation of the
   --  current position into the implementation of the derived iterator types.
   --  This allows an interface to declare an abstract iteration operation
   --  with no knowledge of how to manage the cursor position.
   --
   --  To use the dispatching iterator simply declare an Iterate operation
   --  returning a classwide instance of this type, e.g.:
   --
   --    package Element_Iterators is new
   --       Dispatching_Iterators_Generic (Element_Type);
         --  Element_Type definition not shown.
   --
   --    type IElement_Container is limited interface;
   --
   --    function Iterate
   --       (This : aliased in IIterable)
   --       return Element_Iterators.Iterator'Class;
   --
   --  Each implementation of the IElement_Container interface can then
   --  completely encapsulate all iteration logic in a concrete implementation
   --  of the dispatching iterator.
   --
   --  Users can iterate over the container using generalized for loops:
   --
   --    for Element of Container.Iterate loop
   --       --  ...
   --    end loop;
   --
   --  Ideally, this Iterable type would be defined as an interface rather than
   --  an abstract type, but the Iterable aspect requires primitive operations.
   --  This means it is not possible to implement the internal operations below
   --  as classwide operations on an interface.

   --==========================================================================
   --  Queries
   --==========================================================================

   function Has_Element
     (This : Iterator)
      return Boolean is abstract;
   --
   --  Returns whether the current point of iteration refers to an element.
   --  Exceptions: None

   function Current_Element
     (This : Iterator)
      return Element_Type is abstract
     with
       Pre'Class => This.Has_Element;
   --
   --  Returns the element at the current point of iteration.
   --  Exceptions: None

   --==========================================================================
   --  Commands
   --==========================================================================

   procedure Next
     (This : not null access Iterator) is abstract;
   --
   --  Advances the iterator to the next element.
   --  Exceptions: None

   --==========================================================================
   --  Internal
   --==========================================================================
   --
   --  The following operations have been defined to satisfy the Iterable
   --  aspect. They should not be overridden by derived types.

   type Cursor (<>) is private;

   function Get_First
     (This : aliased Iterator)
      return Cursor;

   function Next
     (This     : Iterator;
      Position : Cursor)
      return Cursor;

   function Has_Element
     (This     : Iterator;
      Position : Cursor)
      return Boolean;

   function Get_Element
     (This     : Iterator;
      Position : Cursor)
      return Element_Type;

private

   type Cursor (Ref : not null access Iterator'Class) is null record;

   type Iterator is abstract tagged limited
      record
         M_Cursor : Cursor (Iterator'Access);
      end record;

end Dispatching_Iterators_Generic;
