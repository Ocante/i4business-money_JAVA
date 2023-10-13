package br.com.i4business.money.application;

import br.com.i4business.money.domain.data.Identifier;
import org.junit.jupiter.api.extension.BeforeEachCallback;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.api.extension.ExtensionContext;
import org.mockito.Mockito;
import org.junit.jupiter.api.Tag;
import org.mockito.junit.jupiter.MockitoExtension;

import br.com.i4business.money.application.UseCaseTest;
import br.com.i4business.money.domain.data.categoria.CategoriaGateway;
import br.com.i4business.money.domain.data.item.ItemGateway;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import java.util.ArrayList;
import java.util.Objects;

import static org.mockito.AdditionalAnswers.returnsFirstArg;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.argThat;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.when;


import java.util.List;
import java.util.Set;
import java.util.stream.Collectors;
@ExtendWith(MockitoExtension.class)
@Tag("unitTest")
public abstract class UseCaseTest implements BeforeEachCallback {

    @Override
    public void beforeEach(final ExtensionContext context) throws Exception {
        Mockito.reset(getMocks().toArray());
    }

    protected abstract List<Object> getMocks();

    protected Set<String> asString(final Set<? extends Identifier> ids) {
        return ids.stream()
                .map(Identifier::getValue)
                .collect(Collectors.toSet());
    }

    protected List<String> asString(final List<? extends Identifier> ids) {
        return ids.stream()
                .map(Identifier::getValue)
                .toList();
    }

    public class CadastrarCategoriaUseCaseTest extends UseCaseTest {

        // Injeta a classe de(UseCase)
        @InjectMocks
        private DefaultCadastrarCategoriaUseCase useCase;

        // Cria um mock para a interface ou classe que representa o gateway da Categoria
        @Mock
        private CategoriaGateway categoriaGateway;

        // Cria um mock para a interface ou classe que representa o gateway do Item
        @Mock
        private ItemGateway itemGateway;

        // Implementando o método para fornecer os mocks criados
        @Override
        protected List<Object> getMocks() {
            return List.of(categoriaGateway, itemGateway);
        }

        @BeforeEach
        public void setUp() {
            Mockito.reset(categoriaGateway, itemGateway);
        }

        // Testa o caminho feliz - inserindo uma categoria válida
        @Test
        public void givenAValidCommand_whenCallsCreateCategory_shouldReturnCategoryId() {
            final var expectedName = "Filmes";
            final var expectedDescription = "A categoria mais assistida";
            final var expectedIsActive = true;

            // Criando um comando com os valores esperados
            final var aCommand = CadastrarCategoriaCommand.com(expectedName, expectedDescription, expectedIsActive, new ArrayList<>());

            // Configura o comportamento do mock para o gateway da Categoria
            when(categoriaGateway.cadastrar(any())).thenAnswer(returnsFirstArg());

            // Executa o caso de uso
            final var actualOutput = useCase.execute(aCommand);

            // Verifica se a saída não é nula e se possui um ID
            Assertions.assertNotNull(actualOutput);
            Assertions.assertNotNull(actualOutput.id());

            // Verifica se o método do gateway foi chamado com os parâmetros esperados
            Mockito.verify(categoriaGateway, times(1)).cadastrar(argThat(aCategory ->
                    Objects.equals(expectedName, aCategory.getNome())
                            && Objects.equals(expectedDescription, aCategory.getDescricao())
                            && Objects.equals(expectedIsActive, aCategory.isAtivo())
                            && Objects.nonNull(aCategory.getId())
                            && Objects.nonNull(aCategory.getCriadoEm())
                            && Objects.nonNull(aCategory.getAlteradoEm())
                            && Objects.isNull(aCategory.getDeletadoEm())
            ));
        }

        // Teste passando uma propriedade inválida (name) - deve lançar uma exceção
        @Test
        public void givenAnInvalidCommand_whenCallsCreateCategory_shouldThrowException() {
            final var invalidName = ""; // Nome inválido

            // Cria um comando com o nome inválido
            final var aCommand = CadastrarCategoriaCommand.com(invalidName, "Descrição válida", true, new ArrayList<>());

            // Configura o comportamento do mock para o gateway da Categoria para lançar uma exceção
            when(categoriaGateway.cadastrar(any())).thenThrow(IllegalArgumentException.class);

            // Verifica se o caso de uso lança uma exceção ao chamar o comando inválido
            Assertions.assertThrows(IllegalArgumentException.class, () -> useCase.execute(aCommand));
        }

        // Teste criando uma categoria inativa
        @Test
        public void givenAnInactiveCategory_whenCallsCreateCategory_shouldReturnCategoryId() {
            // Define os valores esperados para uma categoria inativa
            final var expectedName = "Categoria Inativa";
            final var expectedDescription = "Uma categoria inativa";
            final var expectedIsActive = false;

            // Cria um comando com os valores esperados
            final var aCommand = CadastrarCategoriaCommand.com(expectedName, expectedDescription, expectedIsActive, new ArrayList<>());

            // Configura o comportamento do mock para o gateway da Categoria
            when(categoriaGateway.cadastrar(any())).thenAnswer(returnsFirstArg());

            // Executa o caso de uso
            final var actualOutput = useCase.execute(aCommand);

            // Verifique se a saída não é nula e se possui um ID
            Assertions.assertNotNull(actualOutput);
            Assertions.assertNotNull(actualOutput.id());

            // Verifique se o método do gateway foi chamado com os parâmetros esperados
            Mockito.verify(categoriaGateway, times(1)).cadastrar(argThat(aCategory ->
                    Objects.equals(expectedName, aCategory.getNome())
                            && Objects.equals(expectedDescription, aCategory.getDescricao())
                            && Objects.equals(expectedIsActive, aCategory.isAtivo())
                            && Objects.nonNull(aCategory.getId())
                            && Objects.nonNull(aCategory.getCriadoEm())
                            && Objects.nonNull(aCategory.getAlteradoEm())
                            && Objects.isNull(aCategory.getDeletadoEm())
            ));
        }
    }

}
