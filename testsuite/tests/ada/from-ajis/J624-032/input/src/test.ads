package Test is

   type Root is abstract tagged null record;

   function F (V : Root) return Integer is abstract;
   function F (V : Root) return Float is abstract;

   type Child is new Root with null record;

   function F (V : Child) return Integer;
   function F (V : Child) return Float;

end Test;
