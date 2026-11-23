package Test is

   type Root is abstract tagged record
      C : Integer;
   end record;

   function "=" (Left, Right : Root) return Boolean is abstract;
   function ">" (Left, Right : Root) return Boolean is abstract;
   function "<" (Left, Right : Root) return Boolean is abstract;
   function ">=" (Left, Right : Root) return Boolean is abstract;
   function "<=" (Left, Right : Root) return Boolean is abstract;
   function "or" (Left, Right : Root) return Root is abstract;
   function "and" (Left, Right : Root) return Root is abstract;
   function "xor" (Left, Right : Root) return Root is abstract;
   function "+" (Left, Right : Root) return Root is abstract;
   function "-" (Left, Right : Root) return Root is abstract;
   function "/" (Left, Right : Root) return Root is abstract;
   function "*" (Left, Right : Root) return Root is abstract;
   function "**" (Left, Right : Root) return Root is abstract;

   procedure Call_Operators (Left, Right : Root'Class);

end Test;
