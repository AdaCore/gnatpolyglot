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

package Hello is

   type T is tagged null record;

   subtype ST1 is T'Class;
   subtype ST2 is ST1;

   type AST1 is access all ST1;
   type AST2 is access all ST2;

   function F1 return ST1;
   function FA1 return access ST1;
   function F2 return ST2;
   function FA2 return access ST2;

   procedure P1 (PI : ST1;
                 PIO : in out ST1;
                 PO : out ST1;
                 PIA : access ST1;
                 PIOA : in out AST1;
                 POA : out AST1);

   procedure P2 (PI : ST2;
                 PIO : in out ST2;
                 PO : out ST2;
                 PIA : access ST2;
                 PIOA : in out AST2;
                 POA : out AST2);

end Hello;
