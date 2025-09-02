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

describe('Bot e2e test', () => {
  const botPageUrl = '/bot';
  const botPageUrlPattern = new RegExp('/bot(\\?.*)?$');
  const username = Cypress.env('E2E_USERNAME') ?? 'user';
  const password = Cypress.env('E2E_PASSWORD') ?? 'user';
  const botSample = { name: 'naturally' };

  let bot;

  beforeEach(() => {
    cy.login(username, password);
  });

  beforeEach(() => {
    cy.intercept('GET', '/api/bots+(?*|)').as('entitiesRequest');
    cy.intercept('POST', '/api/bots').as('postEntityRequest');
    cy.intercept('DELETE', '/api/bots/*').as('deleteEntityRequest');
  });

  afterEach(() => {
    if (bot) {
      cy.authenticatedRequest({
        method: 'DELETE',
        url: `/api/bots/${bot.id}`,
      }).then(() => {
        bot = undefined;
      });
    }
  });

  it('Bots menu should load Bots page', () => {
    cy.visit('/');
    cy.clickOnEntityMenuItem('bot');
    cy.wait('@entitiesRequest').then(({ response }) => {
      if (response?.body.length === 0) {
        cy.get(entityTableSelector).should('not.exist');
      } else {
        cy.get(entityTableSelector).should('exist');
      }
    });
    cy.getEntityHeading('Bot').should('exist');
    cy.url().should('match', botPageUrlPattern);
  });

  describe('Bot page', () => {
    describe('create button click', () => {
      beforeEach(() => {
        cy.visit(botPageUrl);
        cy.wait('@entitiesRequest');
      });

      it('should load create Bot page', () => {
        cy.get(entityCreateButtonSelector).click();
        cy.url().should('match', new RegExp('/bot/new$'));
        cy.getEntityCreateUpdateHeading('Bot');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', botPageUrlPattern);
      });
    });

    describe('with existing value', () => {
      beforeEach(() => {
        cy.authenticatedRequest({
          method: 'POST',
          url: '/api/bots',
          body: botSample,
        }).then(({ body }) => {
          bot = body;

          cy.intercept(
            {
              method: 'GET',
              url: '/api/bots+(?*|)',
              times: 1,
            },
            {
              statusCode: 200,
              body: [bot],
            },
          ).as('entitiesRequestInternal');
        });

        cy.visit(botPageUrl);

        cy.wait('@entitiesRequestInternal');
      });

      it('detail button click should load details Bot page', () => {
        cy.get(entityDetailsButtonSelector).first().click();
        cy.getEntityDetailsHeading('bot');
        cy.get(entityDetailsBackButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', botPageUrlPattern);
      });

      it('edit button click should load edit Bot page and go back', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('Bot');
        cy.get(entityCreateSaveButtonSelector).should('exist');
        cy.get(entityCreateCancelButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', botPageUrlPattern);
      });

      it('edit button click should load edit Bot page and save', () => {
        cy.get(entityEditButtonSelector).first().click();
        cy.getEntityCreateUpdateHeading('Bot');
        cy.get(entityCreateSaveButtonSelector).click();
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', botPageUrlPattern);
      });

      it('last delete button click should delete instance of Bot', () => {
        cy.intercept('GET', '/api/bots/*').as('dialogDeleteRequest');
        cy.get(entityDeleteButtonSelector).last().click();
        cy.wait('@dialogDeleteRequest');
        cy.getEntityDeleteDialogHeading('bot').should('exist');
        cy.get(entityConfirmDeleteButtonSelector).click();
        cy.wait('@deleteEntityRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(204);
        });
        cy.wait('@entitiesRequest').then(({ response }) => {
          expect(response?.statusCode).to.equal(200);
        });
        cy.url().should('match', botPageUrlPattern);

        bot = undefined;
      });
    });
  });

  describe('new Bot page', () => {
    beforeEach(() => {
      cy.visit(`${botPageUrl}`);
      cy.get(entityCreateButtonSelector).click();
      cy.getEntityCreateUpdateHeading('Bot');
    });

    it('should create an instance of Bot', () => {
      cy.get(`[data-cy="name"]`).type('tabulate but archive');
      cy.get(`[data-cy="name"]`).should('have.value', 'tabulate but archive');

      cy.get(`[data-cy="description"]`).type('waist upliftingly');
      cy.get(`[data-cy="description"]`).should('have.value', 'waist upliftingly');

      cy.get(`[data-cy="active"]`).should('not.be.checked');
      cy.get(`[data-cy="active"]`).click();
      cy.get(`[data-cy="active"]`).should('be.checked');

      cy.get(entityCreateSaveButtonSelector).click();

      cy.wait('@postEntityRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(201);
        bot = response.body;
      });
      cy.wait('@entitiesRequest').then(({ response }) => {
        expect(response?.statusCode).to.equal(200);
      });
      cy.url().should('match', botPageUrlPattern);
    });
  });
});
