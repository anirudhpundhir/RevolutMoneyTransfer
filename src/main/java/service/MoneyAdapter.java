package service;

import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonSerializationContext;
import com.google.gson.JsonSerializer;
import org.javamoney.moneta.Money;

import java.lang.reflect.Type;

public class MoneyAdapter implements JsonSerializer<Money> {


    @Override
    public JsonElement serialize(Money src, Type typeOfSrc, JsonSerializationContext context) {
        JsonObject obj = new JsonObject();
        obj.addProperty("amount", src.getNumber().doubleValueExact());
        obj.addProperty("currency", src.getCurrency().getCurrencyCode());

        return obj;
    }
}