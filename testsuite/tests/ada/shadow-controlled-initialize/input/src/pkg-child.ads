package Pkg.Child is

   --  Public extensions of a private controlled type
   type Pub_Inherit is new Priv with null record;

   type Pub_Override is new Priv with null record;

   overriding procedure Initialize (Self : in out Pub_Override);

end Pkg.Child;
