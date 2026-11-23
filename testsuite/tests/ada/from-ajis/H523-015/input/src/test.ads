package Test is

   type Lim_Record is tagged limited record
      V : Integer;
   end record;

   procedure P (V : in out Lim_Record);

end Test;
