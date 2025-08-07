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

describe('Comment e2e test', () => {
  const commentPageUrl = '/comment';
  const commentPageUrlPattern = new RegExp('/comment(\\?.*)?$');
  const username = Cypress.env('E2E_USERNAME') ?? 'user';
  const password = Cypress.env('E2E_PASSWORD') ?? 'user';
  // const commentSample = {};

  let comment;
  // let message;
  // let post;

  beforeEach(() => {
    cy.login(username, password);
  });

  /* Disabled due to incompatibility
  beforeEach(() => {
    // create an instance at the required relationship entity:
    cy.authenticatedRequest({
      method: 'POST',
      url: '/api/messages',
      body: {"senderName":"conceptualize towards where","content":"shush thoughtfully","time":"2025-08-07T14:39:58.107Z"},
    }).then(({ body }) => {
      message = body;
    });
    // create an instance at the required relationship entity:
    cy.authenticatedRequest({
      method: 'POST',
      url: '/api/posts',
      body: {"image":"Li4vZmFrZS1kYXRhL2Jsb2IvaGlwc3Rlci5wbmc=","imageContentType":"unknown","description":"avaricious","time":"2025-08-07T09:43:07.347Z"},
    }).then(({ body }) => {
      post = body;
    });
  });
   */

  beforeEach(() => {
    cy.intercept('GET', '/api/comments+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/comments').as('postEntityRequest');
    cy.intercept('DELETE', '/api/comments/*').as('deleteEntityRequest');
  });

  /* Disabled due to incompatibility
  beforeEach(() => {
    // Simulate relationships api for better performance and reproducibility.
    cy.intercept('GET', '/api/messages', {
      statusCode: 200,
      body: [message],
    });

    cy.intercept('GET', '/api/users', {
      statusCode: 200,
      body: [],
    });

    cy.intercept('GET', '/api/posts', {
      statusCode: 200,
      body: [post],
    });

    cy.intercept('GET', '/api/profiles', {
      statusCode: 200,
      body: [],
    });

  });
   */

  afterEach(() => {
    if (comment) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/comments/${comment.id}`,
      }).then(() => {
        comment = undefined;
      });
    }
  });

  /* Disabled due to incompatibility
  afterEach(() => {
    if (message) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/messages/${message.id}`,
      }).then(() => {
        message = undefined;
      });
    }
    if (post) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/posts/${post.id}`,
      }).then(() => {
        post = undefined;
      });
    }
  });
   */

  it('Comments menu should load Comments page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('comment');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response?.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('Comment').should('exist');
    cy.url().should('match', commentPageUrlPattern);
  });

  describe('Comment page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(commentPageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create Comment page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/comment/new$'));
        cy.getEntityCreateUpdateHeading('Comment');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', commentPageUrlPattern);
      });
    });

    describe('with existing value', () => {
      /* Disabled due to incompatibility
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/comments',
          body: {
            ...commentSample,
            parent: message,
            post: post,
          },
        }).then(({ body }) => {
          comment = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/comments+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              body: [comment],
            }
          ).as('entitiesRequestInternal');
        });

        cy.visit(commentPageUrl);

        cy.wait('@entitiesRequestInternal');
      });
       */

      beforeEach(function () {
        cy.visit(commentPageUrl);

        cy.wait('@entitiesRequest').then(({ response }) => {
          if (response?.body.length === 0) {
            this.skip();
          }
        });
      });

      it('detail button click should load details Comment page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('comment');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', commentPageUrlPattern);
      });

      it('edit button click should load edit Comment page and go back', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('Comment');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', commentPageUrlPattern);
      });

      it('edit button click should load edit Comment page and save', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('Comment');
        cy.get(entityCreateSaveButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', commentPageUrlPattern);
      });

      // Reason: cannot create a required entity with relationship with required relationships.
      it.skip('last delete button click should delete instance of Comment', () => {
        cy.get(entityDeleteButtonSelector).last().click();
        cy.getEntityDeleteDialogHeading('comment').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', commentPageUrlPattern);

        comment = undefined;
      });
    });
  });

  describe('new Comment page', () => {
    beforeEach(() => {
      cy.visit(`${commentPageUrl}`);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('Comment');
    });

    // Reason: cannot create a required entity with relationship with required relationships.
    it.skip('should create an instance of Comment', () => {
      cy.get(`[data-cy="parent"]`).select(1);
      cy.get(`[data-cy="post"]`).select(1);

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(201);
        comment = response.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(200);
      });
      cy.url().should('match', commentPageUrlPattern);
    });
  });
});
