import {
  entityConfirmDeleteButtonSelector,
  entityCreateButtonSelector,
  entityCreateCancelButtonSelector,
  entityCreateSaveButtonSelector,
  entityDeleteButtonSelector,
  entityDetailsBackButtonSelector,
  entityDetailsButtonSelector,
  entityEditButtonSelector,
  entityTableSelector,
} from '../../support/entity';

describe('Chat e2e test', () => {
  const chatPageUrl = '/chat';
  const chatPageUrlPattern = new RegExp('/chat(\\?.*)?$');
  const username = Cypress.env('E2E_USERNAME') ?? 'user';
  const password = Cypress.env('E2E_PASSWORD') ?? 'user';
  const chatSample = {};

  let chat;

  beforeEach(() => {
    cy.login(username, password);
  });

  beforeEach(() => {
    cy.intercept('GET', '/api/chats+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/chats').as('postEntityRequest');
    cy.intercept('DELETE', '/api/chats/*').as('deleteEntityRequest');
  });

  afterEach(() => {
    if (chat) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/chats/${chat.id}`,
      }).then(() => {
        chat = undefined;
      });
    }
  });

  it('Chats menu should load Chats page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('chat');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response?.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('Chat').should('exist');
    cy.url().should('match', chatPageUrlPattern);
  });

  describe('Chat page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(chatPageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create Chat page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/chat/new$'));
        cy.getEntityCreateUpdateHeading('Chat');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', chatPageUrlPattern);
      });
    });

    describe('with existing value', () => {
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/chats',
          body: chatSample,
        }).then(({ body }) => {
          chat = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/chats+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              body: [chat],
            },
          ).as('entitiesRequestInternal');
        });

        cy.visit(chatPageUrl);

        cy.wait('@entitiesRequestInternal');
      });

      it('detail button click should load details Chat page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('chat');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', chatPageUrlPattern);
      });

      it('edit button click should load edit Chat page and go back', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('Chat');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', chatPageUrlPattern);
      });

      it('edit button click should load edit Chat page and save', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('Chat');
        cy.get(entityCreateSaveButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', chatPageUrlPattern);
      });

      it('last delete button click should delete instance of Chat', () => {
        cy.get(entityDeleteButtonSelector).last().click();
        cy.getEntityDeleteDialogHeading('chat').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', chatPageUrlPattern);

        chat = undefined;
      });
    });
  });

  describe('new Chat page', () => {
    beforeEach(() => {
      cy.visit(`${chatPageUrl}`);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('Chat');
    });

    it('should create an instance of Chat', () => {
      cy.get(`[data-cy="initiatorName"]`).type('fooey');
      cy.get(`[data-cy="initiatorName"]`).should('have.value', 'fooey');

      cy.get(`[data-cy="accepted"]`).should('not.be.checked');
      cy.get(`[data-cy="accepted"]`).click();
      cy.get(`[data-cy="accepted"]`).should('be.checked');

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(201);
        chat = response.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(200);
      });
      cy.url().should('match', chatPageUrlPattern);
    });
  });
});
