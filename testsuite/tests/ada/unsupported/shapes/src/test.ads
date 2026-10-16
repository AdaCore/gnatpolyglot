package Test is

    type Kind is (A, B, C);

    type R (K : Kind) is record
        case K is
            when A =>
                I : Integer;

            when others =>
                J : Integer;
        end case;
    end record;

end Test;
