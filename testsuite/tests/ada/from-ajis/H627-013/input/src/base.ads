package Base is

   --  Move that into an other file.
   type A_Returned_Type is tagged private;

private

   type A_Returned_Type is tagged record
      null;
   end record;

end Base;
