public class UserFactory {
    public static UserType getUserType(TypesUserTypes type) {
        UserType toReturn = null;
        switch (type) {
            case DOUBLE:
                toReturn = new UserDouble();
                break;
            case GPSCOORDINATE:
                toReturn = new Gpscordinate();
                break;
            default:
                System.out.println("Ошибка: передан неверный тип!");
        }
        return toReturn;
    }
}
