with Base;
package Sample is
   type Child is new Base.Opaque with null record;
   type Grandchild is new Child with null record;
   type Indefinite_Child is new Base.Indefinite with null record;
   type Private_Extension (<>) is new Base.Opaque with private;
   type Forward (<>) is tagged;
   type Forward (N : Positive) is tagged null record;
   type Plain is tagged null record;
   type Known (N : Positive) is tagged null record;
   type Known_Child is new Known with null record;
   type Defaulted (N : Positive := 1) is tagged limited null record;
private
   type Private_Extension is new Base.Opaque with null record;
end Sample;
