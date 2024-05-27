# Joc de Moară în Java

Acest proiect implementează un joc de moară (Nine Men's Morris) în Java, utilizând TCP pentru comunicarea între client și server.

## Structura Proiectului

- **ClientJoc.java**: Gestionează interacțiunea utilizatorului cu jocul, inclusiv afișarea plăcii, plasarea și mutarea pieselor, formarea unei moare și eliminarea pieselor adversarului.
- **ServerJoc.java**: Gestionează conexiunile client și coordonează comunicarea între diferiți clienți.
- **ClientHandler.java**: Gestionează comunicarea pentru fiecare client conectat la server.

## Cerințe

- Java Development Kit (JDK)

## Compilare și Rulare

### Clonarea repo-ului
    1.Clonarea repo-ului
    ```bash
    git clone https://github.com/clauf14/Nine-Men-s-Morris-in-Network-TCP.git
    ```
    2.Acceseaza folderul prin comanda
    ```bash
        cd /path_to_folder
    ```

### Compilare

1. Asigură-te că ai instalat JDK.
2. Deschide un terminal și navighează la directorul unde sunt salvate fișierele `ServerJoc.java`, `ClientJoc.java` și `ClientHandler.java`.
3. Compilează fișierele folosind comanda:
    ```bash
    javac ServerJoc.java ClientJoc.java ClientHandler.java
    ```

### Rulare

#### Pornirea Serverului

1. Rulează serverul folosind comanda:
    ```bash
    java ServerJoc
    ```

#### Pornirea Clientului

1. În alte doua terminale, navighează la același director și rulează clientul:
    - Pentru a te conecta la server pe localhost:
        ```bash
        java ClientJoc
        ```
    - Pentru a te conecta la un server pe o adresă IP specifică:
        ```bash
        java ClientJoc [adresa_IP]
        ```

## Funcționalități

- **Plasarea pieselor:** Jucătorii pot plasa piesele pe tabla de joc.
- **Mutarea pieselor:** După ce toate piesele sunt plasate, jucătorii pot muta piesele pe tablă.
- **Formarea unei moare:** Dacă un jucător formează o moară (trei piese aliniate), poate elimina o piesă adversă.
- **Eliminarea pieselor:** Jucătorii pot elimina piesele adversarului când formează o moară.

## Structuri de Date Folosite

- **`char[][]`**: Placa de joc este reprezentată ca un array bidimensional de caractere, unde fiecare element reprezintă o celulă a plăcii de joc.
- **`ArrayList<ClientHandler>`**: Folosit pentru a gestiona clienții conectați la server.

## Comunicarea în Rețea

- Comunicarea între client și server este realizată folosind `Socket` și `ServerSocket` pentru conexiunile TCP.
- Serverul ascultă conexiunile pe un port specific (55000) și creează un nou thread pentru fiecare client conectat.
- Mesajele sunt trimise între client și server folosind `BufferedReader` și `BufferedWriter`.

## Structuri de Limbaj Java Explicate

| Structură de limbaj             | Explicație                                                                                               |
|---------------------------------|----------------------------------------------------------------------------------------------------------|
| `Socket`                        | Folosit pentru a crea o conexiune TCP între client și server.                                            |
| `ServerSocket`                  | Folosit de server pentru a asculta conexiuni pe un anumit port și a accepta conexiuni de la clienți.     |
| `BufferedReader`                | Folosit pentru a citi date de la client/server într-un mod eficient, linie cu linie.                     |
| `BufferedWriter`                | Folosit pentru a trimite date către client/server, permițând scrierea eficientă a datelor în flux.       |
| `ArrayList<ClientHandler>`      | Folosit pentru a gestiona lista clienților conectați la server, permițând trimiterea mesajelor către toți.|
| `char[][]`                      | Folosit pentru a reprezenta placa de joc, unde fiecare element este o celulă care poate fi goală sau ocupată.|
| `Thread`                        | Folosit pentru a crea un nou fir de execuție pentru fiecare client, permițând comunicarea concurentă.      |

## Concluzie

Acest proiect demonstrează cum poate fi implementat un joc de moară folosind Java și TCP pentru a gestiona comunicarea între mai mulți clienți conectați la un server central. Structurile de date precum array-urile bidimensionale și listele dinamice, împreună cu conceptele de programare concurentă și comunicare în rețea, sunt utilizate pentru a realiza o experiență de joc interactivă și robustă.

## Autori

- Numele tău
- Colaboratori (dacă este cazul)

## Licență

Acest proiect este licențiat sub licența MIT - vezi fișierul [LICENSE](LICENSE) pentru detalii.
