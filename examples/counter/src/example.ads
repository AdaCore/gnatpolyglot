package Example is

    type Counter is record
        Count : Natural := 0;
    end record;

    procedure Increment (C : in out Counter);
    procedure Increment (C : in out Counter; N: Natural);

    procedure Reset (C: in out Counter);

end Example;
