package Hello is

   procedure Say (Something : String);

   type SQL_Field (Table, Instance : Positive;
                   Instance_Index  : Integer)
      is null record;

end Hello;
