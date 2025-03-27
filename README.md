# people-management-api
Built with Spring Boot 3, the system manages various person types and enables flexible searching via Criteria API. It ensures secure access with Spring Security and versioned DB migrations using Liquibase. The Facade pattern enhances flexibility, allowing extensions without modifying existing code. Designed for scalability and concurrency safety.


System zarządzania osobami to aplikacja webowa oparta na Spring Boot 3, umożliwiająca przechowywanie i wyszukiwanie danych różnych typów osób, takich jak pracownicy, studenci i emeryci. System oferuje pojedynczy, uniwersalny endpoint do pobierania osób według różnych kryteriów, obsługując paginację oraz filtrowanie po numerze PESEL, imieniu, nazwisku, wieku, wzroście, wadze i innych specyficznych parametrach, które zostało zaimplementowane z wykorzystaniem Criteria API. Dodawanie nowych typów osób nie wymaga modyfikacji istniejących klas, a walidacja oraz obsługa błędów zapewniają poprawność danych. Edycja osoby obsługuje mechanizm optymistycznej blokady, chroniąc przed problemami współbieżności i "missing update". System umożliwia zarządzanie stanowiskami pracowników, zapobiegając nakładaniu się dat zatrudnienia. Dodatkowo, aplikacja obsługuje asynchroniczny import danych z plików CSV o dużej objętości, działając w sposób transakcyjny i wydajny (min. 50 tys. insertów/s dla H2), a do kolejkowania importu wykorzystuje Redis.

Aby zapewnić maksymalną elastyczność i łatwą rozszerzalność systemu w przyszłości, wykorzystano wzorzec fasady, który upraszcza interakcję z modułami aplikacji i umożliwia ich łatwe rozwijanie. Dodatkowo, do zarządzania migracjami bazy danych użyto Liquibase, co pozwala na kontrolowane i wersjonowane zmiany w schemacie danych. Zabezpieczenia aplikacji są oparte na Spring Security, kontrolując dostęp do funkcji systemu. System został zaprojektowany do pracy w środowisku skalowalnym i wieloinstancyjnym, zapewniając odporność na problemy współbieżności.

-----------------------------------------------------------------------------------
        @RequestBody do filtrowania:   @PostMapping("/search")
-----------------------------------------------------------------------------------
{
  "criteria": [
{"key": "numberOfPositions", "operation": "range", "value": [0,1]},
{"key": "weight", "operation": "range", "value": [50,60]},
{"key": "gender", "operation": "containsIgnoreCase", "value": "f"},
{"key": "university", "operation": "containsIgnoreCase", "value": "state"},
{"key": "name", "operation": "containsIgnoreCase", "value": "John"}
  ]
}
-----------------------------------------------------------------------------------
        CreatePersonCommand:
-----------------------------------------------------------------------------------

    {
      "classType": "Employee",
      "parameters": [
        { "name": "name", "value": "John" },
        { "name": "surname", "value": "Doe" },
        { "name": "pesel", "value": "74032813943" },
        { "name": "height", "value": "180" },
        { "name": "weight", "value": "75" },
        { "name": "email", "value": "john.doe@example.com" }
      ]
    }

    {
      "classType": "Retiree",
      "parameters": [
        { "name": "name", "value": "Jane" },
        { "name": "surname", "value": "Smith" },
        { "name": "pesel", "value": "61022717475" },
        { "name": "height", "value": "165" },
        { "name": "weight", "value": "65" },
        { "name": "email", "value": "jane.smith@example.com" },
        { "name": "pension", "value": "3000" },
        { "name": "yearsWorked", "value": "40" }
      ]
    }

    {
      "classType": "Student",
      "parameters": [
        { "name": "name", "value": "Emily" },
        { "name": "surname", "value": "Johnson" },
        { "name": "pesel", "value": "05212686866" },
        { "name": "height", "value": "170" },
        { "name": "weight", "value": "60" },
        { "name": "email", "value": "emily.johnson@example.com" },
        { "name": "university", "value": "Harvard University" },
        { "name": "academicYear", "value": "3" },
        { "name": "course", "value": "Computer Science" },
        { "name": "scholarship", "value": "2000" }
      ]
    }
-----------------------------------------------------------------------------------
        EditPersonCommand:
-----------------------------------------------------------------------------------

        {
          "id": 1,
          "classType": "Employee",
          "parameters": [
            { "name": "name", "value": "John" },
            { "name": "surname", "value": "Doe" },
            { "name": "pesel", "value": "74032813943" },
            { "name": "height", "value": "180" },
            { "name": "weight", "value": "75" },
            { "name": "email", "value": "john.doe@example.com" }
          ],
         "version": "0"
        }
-----------------------------------------------------------------------------------
        CreatePositionCommand:
-----------------------------------------------------------------------------------

{
        "name": "Chief Coffee Tester",
        "dateFrom": "2024-06-01",
        "dateTo": null,
        "salary": 1000000
        }
