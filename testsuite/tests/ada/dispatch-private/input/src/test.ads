package Test is

   type Priv is private;

   type Root is tagged null record;

   function F (R : Root; P: Priv) return Priv;

   function Inc (P: Priv) return Priv;

   procedure Print (P: Priv);

   function Call_F (R : Root'Class; P: Priv) return Priv;

private

   type Priv is record
      I: Integer := 0;
   end record;

end Test;
