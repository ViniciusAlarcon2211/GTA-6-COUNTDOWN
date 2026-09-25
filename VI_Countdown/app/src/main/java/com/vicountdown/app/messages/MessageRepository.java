package com.vicountdown.app.messages;

import com.vicountdown.app.countdown.CountdownEngine;
import java.time.LocalDate;
import java.util.*;

public final class MessageRepository {
    private MessageRepository() {}
    private static final String[] DAILY = {
        "A espera continua. O horizonte está um pouco mais perto.",
        "Mais um dia riscado do calendário.",
        "Neon aceso. Relógio correndo.",
        "O tempo passa; a expectativa só aumenta.",
        "Cada segundo aproxima a próxima grande noite.",
        "A cidade ainda é uma promessa. A contagem é real.",
        "Menos um dia. Mais perto do lançamento.",
        "A espera também faz parte da história.",
        "O relógio não para — e a data se aproxima.",
        "Hoje falta menos do que ontem. Já é alguma coisa.",
        "Uma contagem simples para uma espera nada simples.",
        "Fique de olho no horizonte. O dia está chegando."
    };

    public static String daily() {
        long n = LocalDate.now().toEpochDay();
        return DAILY[Math.floorMod((int)n, DAILY.length)];
    }

    public static List<Item> all() {
        List<Item> out = new ArrayList<>();
        out.add(new Item("Destaque", daily(), "Hoje"));
        int[] marks = {100,90,60,30,14,7,3,2,1};
        for (int d : marks) {
            out.add(new Item("Marco", milestone(d), CountdownEngine.RELEASE_DATE.minusDays(d).toString()));
        }
        out.add(new Item("Lançamento", "A espera chegou ao fim. Aproveite o lançamento.", "19/11/2026"));
        for (int i=0;i<DAILY.length;i++) out.add(new Item("Diária", DAILY[i], "Mensagem " + (i+1)));
        return out;
    }

    public static String milestone(long d) {
        if (d == 1) return "Amanhã. Depois de tanta espera, falta só um nascer do sol.";
        if (d <= 3) return "Reta final: " + d + " dias. Agora dá para contar nos dedos.";
        if (d <= 14) return d + " dias. A reta final já começou.";
        if (d <= 30) return d + " dias. Um mês já não parece tão longe.";
        return d + " dias. Mais um grande marco ficou para trás.";
    }

    public record Item(String category, String text, String date) {}
}
