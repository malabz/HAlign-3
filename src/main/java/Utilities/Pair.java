package Utilities;

@SuppressWarnings("unused")
public class Pair<first_type, second_type>
{

    first_type first;
    second_type second;

    public Pair(first_type lhs, second_type rhs)
    {
        first = lhs;
        second = rhs;
    }

    public Pair()
    {
        first = null;
        second = null;
    }

    public first_type get_first()
    {
        return first;
    }

    public second_type get_second()
    {
        return second;
    }

    public void set_first(first_type new_value)
    {
        first = new_value;
    }

    public void set_second(second_type new_value)
    {
        second = new_value;
    }
}