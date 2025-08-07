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

describe('Message e2e test', () => {
  const messagePageUrl = '/message';
  const messagePageUrlPattern = new RegExp('/message(\\?.*)?$');
  const username = Cypress.env('E2E_USERNAME') ?? 'user';
  const password = Cypress.env('E2E_PASSWORD') ?? 'user';
  const messageSample = {};

  let message;

  beforeEach(() => {
    cy.login(username, password);
  });

  beforeEach(() => {
    cy.intercept('GET', '/api/messages+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/messages').as('postEntityRequest');
    cy.intercept('DELETE', '/api/messages/*').as('deleteEntityRequest');
  });

  afterEach(() => {
    if (message) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/messages/${message.id}`,
      }).then(() => {
        message = undefined;
      });
    }
  });

  it('Messages menu should load Messages page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('message');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response?.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('Message').should('exist');
    cy.url().should('match', messagePageUrlPattern);
  });

  describe('Message page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(messagePageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create Message page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/message/new$'));
        cy.getEntityCreateUpdateHeading('Message');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', messagePageUrlPattern);
      });
    });

    describe('with existing value', () => {
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/messages',
          body: messageSample,
        }).then(({ body }) => {
          message = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/messages+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              body: [message],
            },
          ).as('entitiesRequestInternal');
        });

        cy.visit(messagePageUrl);

        cy.wait('@entitiesRequestInternal');
      });

      it('detail button click should load details Message page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('message');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', messagePageUrlPattern);
      });

      it('edit button click should load edit Message page and go back', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('Message');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', messagePageUrlPattern);
      });

      it('edit button click should load edit Message page and save', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('Message');
        cy.get(entityCreateSaveButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', messagePageUrlPattern);
      });

      it('last delete button click should delete instance of Message', () => {
        cy.get(entityDeleteButtonSelector).last().click();
        cy.getEntityDeleteDialogHeading('message').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', messagePageUrlPattern);

        message = undefined;
      });
    });
  });

  describe('new Message page', () => {
    beforeEach(() => {
      cy.visit(`${messagePageUrl}`);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('Message');
    });

    it('should create an instance of Message', () => {
      cy.get(`[data-cy="senderName"]`).type('hmph');
      cy.get(`[data-cy="senderName"]`).should('have.value', 'hmph');

      cy.get(`[data-cy="content"]`).type('phooey readily psst');
      cy.get(`[data-cy="content"]`).should('have.value', 'phooey readily psst');

      cy.get(`[data-cy="time"]`).type('2025-08-06T19:45');
      cy.get(`[data-cy="time"]`).blur();
      cy.get(`[data-cy="time"]`).should('have.value', '2025-08-06T19:45');

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(201);
        message = response.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(200);
      });
      cy.url().should('match', messagePageUrlPattern);
    });
  });
});
