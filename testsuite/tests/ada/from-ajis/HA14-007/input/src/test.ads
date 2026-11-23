package Test is

   type Root is tagged record
      Filling : Character := 'x';
   end record;

   procedure Fill (This : Root; Str : in out String);

end Test;
