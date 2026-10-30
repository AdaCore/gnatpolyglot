package Base is
   type Opaque (<>) is tagged private;
   type Indefinite (<>) is tagged private;
private
   type Opaque is tagged null record;
   type Indefinite (N : Positive) is tagged null record;
end Base;
