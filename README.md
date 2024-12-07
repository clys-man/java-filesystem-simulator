## Simulador de Sistema de Arquivos em Java com Journaling

### **Descrição do Projeto**
Este projeto apresenta o desenvolvimento de um simulador de sistema de arquivos, criado para facilitar o entendimento prático do funcionamento de sistemas de arquivos em sistemas operacionais. O simulador implementa operações essenciais de manipulação de arquivos e diretórios, incluindo um sistema de *journaling* para garantir a integridade das operações.

---

### **O que é um Sistema de Arquivos?**
Um sistema de arquivos é uma estrutura utilizada por sistemas operacionais para organizar, armazenar e gerenciar dados de forma eficiente em dispositivos de armazenamento, como discos rígidos ou SSDs. Ele define a forma como os dados são armazenados, recuperados e manipulados.

Os sistemas de arquivos modernos possuem características avançadas, como:
- Suporte a hierarquias de diretórios.
- Permissões de acesso.
- Capacidade de recuperação de falhas com *journaling*.

#### **Tipos Comuns de Sistemas de Arquivos**
- **FAT32, NTFS:** Amplamente utilizados em sistemas Windows.
- **EXT4:** Usado em distribuições Linux.
- **APFS:** Sistema de arquivos da Apple.

---

### **O que é Journaling?**
O *journaling* é uma técnica utilizada em sistemas de arquivos para registrar as operações realizadas, garantindo a integridade dos dados, especialmente em caso de falhas (por exemplo, quedas de energia). Ele funciona criando um log das alterações pendentes antes de aplicá-las ao sistema de arquivos.

#### **Funcionamento do Journaling**
1. **Registro no log:** Antes de realizar uma operação (como criar ou excluir um arquivo), o sistema grava um registro no log, indicando a operação.
2. **Execução da operação:** A operação é então aplicada ao sistema de arquivos.
3. **Confirmação no log:** Após a conclusão bem-sucedida, o registro no log é atualizado para indicar que a operação foi concluída.

##### **Tipos de Journaling:**
- **Write-Ahead Logging:** Garante que as alterações sejam gravadas no log antes de serem aplicadas ao sistema de arquivos.
- **Ordered Journaling:** Apenas as operações de metadados são registradas.
- **Full Journaling:** Tanto os dados quanto os metadados são registrados no log.

No simulador, o *journaling* foi implementado usando um arquivo de log (`journal.log`), que acompanha as operações realizadas no sistema.

---

### **Como Funciona o Simulador?**
O simulador utiliza uma **estrutura de dados em árvore** para representar o sistema de arquivos.

#### **Por que usar uma árvore?**
A organização hierárquica de diretórios e arquivos em sistemas de arquivos reais é naturalmente representada por uma estrutura de árvore.
- Cada nó pode ser um diretório ou arquivo.
- Os diretórios podem conter outros nós (subdiretórios ou arquivos).
- A árvore permite percorrer e manipular a hierarquia com eficiência.

Exemplo de hierarquia:
```  
/  
├── documentos  
│   ├── projetos  
│   └── notas.txt  
└── imagens  
    ├── foto1.jpg  
    └── foto2.png  
```  

No simulador:
- A classe `Directory` representa um nó de diretório.
- A classe `FileNode` representa um arquivo no sistema.
- A navegação ocorre seguindo as referências de pai/filho na estrutura.

---

### **Arquitetura do Simulador**

#### **Estrutura de Dados**
- **`Directory`:** Representa diretórios, armazenando referências para subdiretórios e arquivos.
- **`FileNode`:** Representa arquivos com nome e conteúdo.
- **`Main`:** Gerencia a interação com o usuário, processando comandos.
- **`Journal`:** Registra as operações em um arquivo de log para garantir a integridade.

#### **Exemplo de Código - Journaling**
O *journaling* é implementado com etapas de registro e conclusão:
```java  
public void logOperation(String operation) {  
    try (FileWriter logWriter = new FileWriter("journal.log", true)) {  
        logWriter.write(operation + System.lineSeparator());  
    } catch (IOException e) {  
        System.out.println("Erro ao registrar a operação no log.");  
    }  
}  
```  

#### **Exemplo de Código - Navegação na Estrutura**
A navegação na árvore segue caminhos especificados pelo usuário:
```java  
public Directory navigate(String path) {  
    Directory current = root;  
    String[] parts = path.split("/");  
    for (String part : parts) {  
        if (!part.isEmpty() && current != null) {  
            current = current.getSubdirectory(part);  
        }  
    }  
    return current;  
}  
```  

---

### **Como Executar o Simulador**

#### **Pré-requisitos**
- JDK 21 ou superior.

#### **Passos para Execução**
1. Clone o repositório:
   ```bash  
   git clone https://github.com/clys-man/java-filesystem-simulator 
   cd java-filesystem-simulator
   ```  
2. Compile e execute o programa:
   ```bash  
   javac -d bin src/edu/clysman/unifor/filesystem/*.java  
   java -cp bin edu.clysman.unifor.filesystem.Main  
   ```  

#### **Exemplo de Uso**
```  
> mkdir documentos  
Diretório criado com sucesso.  
> touch documentos/notas.txt  
Arquivo criado com sucesso.  
> write "Olá Mundo" documentos/notas.txt  
Conteúdo gravado no arquivo.  
> cat documentos/notas.txt  
Olá Mundo  
```  

---

### **Conclusão**
Este simulador de sistema de arquivos oferece uma visão prática e funcional do gerenciamento de arquivos e diretórios. A implementação de uma estrutura de dados em árvore reflete a hierarquia típica de sistemas de arquivos, enquanto o sistema de *journaling* adiciona confiabilidade e integridade às operações, simulando um ambiente robusto e resiliente.

O projeto destaca como as operações básicas de um sistema operacional podem ser implementadas em um contexto controlado e educativo, permitindo que estudantes e desenvolvedores compreendam melhor conceitos fundamentais de sistemas de arquivos e os desafios associados ao seu design.

----

### **Autores**
- Clysman Alves
- Pedro Henrique do Nascimento Lins