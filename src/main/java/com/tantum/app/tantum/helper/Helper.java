package com.tantum.app.tantum.helper;

import java.util.Arrays;
import java.util.List;

public class Helper {

	public static String course_test = "{\"subjects\":[{\"nome\":\"Materia 1\",\"codigo\":\"INE5666\",\"fase\":1,\"professor\":\"Professor aleatório\",\"aulas\":2,\"obrigatoria\":true,\"horarios\":[\"3.0820-2 / CTC-CTC102\"],\"requisitos\":[]},{\"nome\":\"Materia 2\",\"codigo\":\"INE5661\",\"fase\":1,\"professor\":\"Professor aleatório\",\"aulas\":2,\"obrigatoria\":true,\"horarios\":[\"3.1620-2 / CTC-CTC102\"],\"requisitos\":[]},{\"nome\":\"Materia 3\",\"codigo\":\"INE1111\",\"fase\":1,\"professor\":\"Professor aleatório\",\"aulas\":2,\"obrigatoria\":true,\"horarios\":[\"2.1330-4 / CTC-CTC102\"],\"requisitos\":[]},{\"nome\":\"Materia 4\",\"codigo\":\"INE5656\",\"fase\":2,\"professor\":\"Professor aleatório\",\"aulas\":2,\"obrigatoria\":true,\"horarios\":[\"2.0820-2 / CTC-CTC102\"],\"requisitos\":[\"INE5666\"]},{\"nome\":\"Materia 5\",\"codigo\":\"INE5156\",\"fase\":2,\"professor\":\"Professor aleatório\",\"aulas\":2,\"obrigatoria\":true,\"horarios\":[\"2.1010-2 / CTC-CTC102\"],\"requisitos\":[]},{\"nome\":\"Materia 6\",\"codigo\":\"INE5612\",\"fase\":2,\"professor\":\"Professor aleatório\",\"aulas\":2,\"obrigatoria\":true,\"horarios\":[\"5.1330-3 / CTC-CTC102\"],\"requisitos\":[]},{\"nome\":\"Materia 7\",\"codigo\":\"INE5688\",\"fase\":3,\"professor\":\"Professor aleatório\",\"aulas\":2,\"obrigatoria\":true,\"horarios\":[\"5.1330-3 / CTC-CTC102\"],\"requisitos\":[\"INE5612\"]},{\"nome\":\"Materia 8\",\"codigo\":\"INE5132\",\"fase\":3,\"professor\":\"Professor aleatório\",\"aulas\":2,\"obrigatoria\":true,\"horarios\":[\"2.1010-2 / CTC-CTC102\"],\"requisitos\":[\"INE5656\"]},{\"nome\":\"Materia 9\",\"codigo\":\"INE5133\",\"fase\":4,\"professor\":\"Professor aleatório\",\"aulas\":2,\"obrigatoria\":true,\"horarios\":[\"4.1620-2 / CTC-CTC102\"],\"requisitos\":[]},{\"nome\":\"Materia 10\",\"codigo\":\"INE5134\",\"fase\":4,\"professor\":\"Professor aleatório\",\"aulas\":2,\"obrigatoria\":true,\"horarios\":[\"2.1010-2 / CTC-CTC102\"],\"requisitos\":[\"INE5132\",\"INE5661\"]}]}";

	public static String class_history_test = "{\"semesters\":[{\"semester\":\"2015-1\",\"subjects\":[\"INE5666\",\"INE5661\",\"INE1111\"]},{\"semester\":\"2015-2\",\"subjects\":[\"INE5656\",\"INE5156\"]}]}";

	public static List<String> semesters = Arrays.asList("2018-1", "2018-2", "2019-1", "2019-2", "2020-1", "2020-2", "2021-1", "2021-2");

}
