package Floats is

   type T is tagged null record;

   function Inc (Obj: T; F: Float) return Float is (F + 1.0);

   function Call_Inc (Obj: T'Class; F: Float) return Float is (Obj.Inc (F));

end Floats;
