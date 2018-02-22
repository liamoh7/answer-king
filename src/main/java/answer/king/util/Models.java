package answer.king.util;

import answer.king.error.NotFoundException;

public class Models {

    private Models() {

    }

    public static <T> T throwNotFoundIfNull(T model) throws NotFoundException {
        if (model == null) throw new NotFoundException();
        return model;
    }
}
