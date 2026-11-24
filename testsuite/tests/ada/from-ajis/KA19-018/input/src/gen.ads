

generic

package Gen is

   type Cursor is null record;

   type List is tagged private;

   function Copy (Source : List) return List;

   procedure Iterate
     (Container : List;
      Process   : not null access procedure (Position : Cursor));

   procedure Reverse_Iterate
     (Container : List;
      Process   : not null access procedure (Position : Cursor));


private

   type List is tagged null record;

end Gen;
