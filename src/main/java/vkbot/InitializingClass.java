package vkbot;

import java.io.BufferedReader;
import java.io.FileReader;

import org.quartz.CronScheduleBuilder;
import org.quartz.JobBuilder;
import org.quartz.JobDetail;
import org.quartz.Scheduler;
import org.quartz.Trigger;
import org.quartz.TriggerBuilder;
import org.quartz.impl.StdSchedulerFactory;

import com.vk.api.sdk.client.TransportClient;
import com.vk.api.sdk.client.VkApiClient;
import com.vk.api.sdk.client.actors.GroupActor;
import com.vk.api.sdk.httpclient.*;

import vkbot.handlers.*;
import vkbot.jobs.TablesUpdatingJob;
import vkbot.sql.RowArray;
import vkbot.sql.SelectSQLRequest;
import vkbot.state.ErrorState;
import vkbot.state.NullState;
import vkbot.state.State;

public class InitializingClass {
	public static final String START_MESSAGE = "Привет! Я бот, который поможет тебе найти попутчиков (на данный момент доступно направление Уфа-Кармаскалы).\n\nИз функций сейчас имеется:\n- автоматическое уведомление о появившейся поездке. Нужно пройти по пунктам меню до списка всех поездок, после чего можно будет настроить. Наличие включенной опции будет указано в главном меню, оттуда же ее можно будет отключить.\n- автоматическая публикация поездки. Нужно пройти по пунктам меню до публикации поездки, задать время, номер телефона (если есть), заметку, после чего будет возможность включить автоматическую публикацию. Сейчас доступна еженедельная публикация и публикация на текущую неделю. Наличие включенной опции будет показано в главном меню, откуда ее можно будет отключить.\n\nПо всем вопросам/предложениям писать разработчику: https://vk.com/id84951026.\n";
	
	public static void main(String[] args) {
		TransportClient transportClient = HttpTransportClient.getInstance();
		VkApiClient vk = new VkApiClient(transportClient);
		ProcessingCenter pCenter = ProcessingCenter.getInstance();
		pCenter.setPassFileName("S:/WorkSpace/vkbot/bott/sqlpass.txt").setDriver("com.mysql.cj.jdbc.Driver").setUrl("jdbc:mysql://127.0.0.1:3306/");
		
		/*
		 * инициализация дерева состояний
		 * для собственного удобства использовал табуляцию
		 * некоторые анонимные обработчики и состояния созданы прямо в инициализации дерева
		 * 
		 * ВНИМАНИЕ!!
		 * ДАЛЬШЕ ИДЕТ ОЧЕНЬ СТРАШНАЯ ЧАСТЬ
		 * НЕПОНЯТНО И ЗАПУТАННО (ВО ВСЯКОМ СЛУЧАЕ ДЛЯ САМОГО РАЗРАБОТЧИКА)
		 */
		
		/*
		 * обработчик для диалога ввода направления
		 * (создает объект поездки с нужным направлением)
		 */
		
		
		MessageHandler directionHandler = new MessageHandler() {
			@Override
			public int handle(SimpleMessenger messenger, MessageStandardClass message, State state) {
				ProcessingCenter pCenter = ProcessingCenter.getInstance();
				String userId = message.getUserId();
				String text = message.getText();
				try {
					int direction = Integer.parseInt(text);
					if (direction <= Fleight.getDirectionsCount() && direction > 0) {
						Fleight fleight = new Fleight().setDirection(direction).setUserId(userId);
						pCenter.addIncompletedFleight(userId, fleight);
						pCenter.setState(messenger, userId, state.get(1));
						return 1;
					}
				} catch (Exception e) {
					
				}
				return this.getNext().handle(messenger, message, state);
			}
		};
		
		MessageHandler directionHandler1 = new MessageHandler() {
			@Override
			public int handle(SimpleMessenger messenger, MessageStandardClass message, State state) {
				ProcessingCenter pCenter = ProcessingCenter.getInstance();
				String userId = message.getUserId();
				String text = message.getText();
				try {
					int direction = Integer.parseInt(text);
					if (direction <= Fleight.getDirectionsCount() && direction > 0) {
						Fleight fleight = new Fleight().setDirection(Integer.parseInt(text)).setUserId(userId);
						pCenter.addIncompletedFleight(userId, fleight);
						pCenter.setState(messenger, userId, state.get(1));
						return 1;
					}
				} catch (Exception e) {
					
				}
				return this.getNext().handle(messenger, message, state);
			}
		};
		
		/*
		 * обработчик ввода дня
		 * (берет существующий незаполненный объект поездки и добавляет день)
		 */
		MessageHandler dayHandler = new MessageHandler() {
			@Override
			public int handle(SimpleMessenger messenger, MessageStandardClass message, State state) {
				ProcessingCenter pCenter = ProcessingCenter.getInstance();
				String userId = message.getUserId();
				
				if (!pCenter.isContainsFleight(userId)) {
					State errState = new ErrorState();
					errState.setMessage("Ошибка. Прошло слишком много времени или система была перезагружена. Нужно начать сначала.");
					pCenter.setState(messenger, userId, errState);
					return -1;
				}
				
				try {
					Integer day = Integer.parseInt(message.getText());
					if (day <= Fleight.getDaysCount() && day > 0) {
						Fleight fleight = pCenter.getIncompletedFleight(userId);
						fleight.setDay(day);
						if (fleight.isExist()) {
							State err = new ErrorState().setMessage("На этот день в данном направлении у тебя уже есть поездка.").setIsBackButtonOn(false);
							err.get(0).setFullId("saved state");
							pCenter.setSavedState(userId, err);
							pCenter.setState(messenger, userId, err);
							return -1;
						}
						pCenter.setState(messenger, message.getUserId(), state.get(1));
						return 1;
					}
				} catch (Exception e) {
					
				}
				return this.getNext().handle(messenger, message, state);
			}
		};
		
		MessageHandler dayHandler1 = new MessageHandler() {
			@Override
			public int handle(SimpleMessenger messenger, MessageStandardClass message, State state) {
				ProcessingCenter pCenter = ProcessingCenter.getInstance();
				String userId = message.getUserId();
				
				if (!pCenter.isContainsFleight(userId)) {
					State errState = new ErrorState();
					errState.setMessage("Ошибка. Прошло слишком много времени или система была перезагружена. Нужно начать сначала.");
					pCenter.setState(messenger, userId, errState);
					return -1;
				}
				
				try {
					Integer day = Integer.parseInt(message.getText());
					if (day <= Fleight.getDaysCount() && day > 0) {
						Fleight fleight = pCenter.getIncompletedFleight(userId);
						fleight.setDay(day);
						pCenter.setState(messenger, message.getUserId(), state.get(1));
						return 1;
					}
				} catch (Exception e) {
					
				}
				return this.getNext().handle(messenger, message, state);
			}
		};
		
		/*
		 * обработчик диалога ввода номера телефона (содержание на совести юзера) 
		 */
		MessageHandler numbHandler = new MessageHandler() {
			@Override
			public int handle(SimpleMessenger messenger, MessageStandardClass message, State state) {
				final ProcessingCenter pCenter = ProcessingCenter.getInstance();
				final String userId = message.getUserId();
				
				if (!pCenter.isContainsFleight(userId)) {
					State errState = new ErrorState();
					errState.setMessage("Ошибка. Прошло слишком много времени или система была перезагружена. Нужно начать сначала.");
					pCenter.setState(messenger, userId, errState);
					return -1;
				}
				
				Fleight fleight = pCenter.getIncompletedFleight(userId);
				fleight.setNumber(message.getText());
				pCenter.setState(messenger, userId, new State("1.1.1.1.1", false) {
					@Override
					public String buildText() {
						String text = "Введи заметку (для удобства можно написать откуда выезжаешь и как едешь).";
						SelectSQLRequest request = new SelectSQLRequest("vk_bot", "user_ids", "root", pCenter.getUrl(), pCenter.getDriver(), pCenter.getPassFileName()).setWhereFields("user_id = " + userId).addSelectingField("saved_note");
						RowArray result = request.execute();
						if (result.next() && (result.getString("saved_note") != null)) {
							String note = result.getString("saved_note");
							text += "\nРанее использованная заметка: " + note + "\n1 - Использовать сохраненную заметку";
						}
						return text;
					}
				});
				return 1;
			}
		};
		
		/*
		 * обработчик ввода сохраненного номера телефона
		 */
		
		MessageHandler selectSavedNumberCommandHandler= new MessageHandler() {
			@Override
			public int handle(SimpleMessenger messenger, MessageStandardClass message, State state) {
				if (message.getText().equals("1")) {
					ProcessingCenter pCenter = ProcessingCenter.getInstance();
					SelectSQLRequest request = new SelectSQLRequest("vk_bot", "user_ids", "root", pCenter.getUrl(), pCenter.getDriver(), pCenter.getPassFileName()).setWhereFields("user_id = " + message.getUserId()).addSelectingField("saved_number");
					RowArray result = request.execute();
					if (result.next() && (result.getString("saved_number") != null)) {
						String number = result.getString("saved_number");
						return this.getNext().handle(messenger, message.setText(number), state);
					}
				}
				return this.getNext().handle(messenger, message, state);
			}
		};
		
		/*
		 * обработчик ввода заметки
		 */
		MessageHandler noteHandler = new MessageHandler() {
			@Override
			public int handle(SimpleMessenger messenger, MessageStandardClass message, State state) {
				ProcessingCenter pCenter = ProcessingCenter.getInstance();
				String userId = message.getUserId();
				
				if (!pCenter.isContainsFleight(userId)) {
					State errState = new ErrorState();
					errState.setMessage("Ошибка. Прошло слишком много времени или система была перезагружена. Нужно начать сначала.");
					pCenter.setState(messenger, userId, errState);
					return -1;
				}
				
				Fleight fleight = pCenter.getIncompletedFleight(userId);
				fleight.setNote(message.getText());
				pCenter.setState(messenger, userId, state.get(1));
				return 1;
			}
		};
		
		/*
		 * обработчик ввода сохраненного номера телефона
		 */
		
		MessageHandler selectSavedNoteCommandHandler= new MessageHandler() {
			@Override
			public int handle(SimpleMessenger messenger, MessageStandardClass message, State state) {
				if (message.getText().equals("1")) {
					ProcessingCenter pCenter = ProcessingCenter.getInstance();
					SelectSQLRequest request = new SelectSQLRequest("vk_bot", "user_ids", "root", pCenter.getUrl(), pCenter.getDriver(), pCenter.getPassFileName()).setWhereFields("user_id = " + message.getUserId()).addSelectingField("saved_note");
					RowArray result = request.execute();
					if (result.next() && (result.getString("saved_note") != null)) {
						String note = result.getString("saved_note");
						return this.getNext().handle(messenger, message.setText(note), state);
					}
				}
				return this.getNext().handle(messenger, message, state);
			}
		};
		
		/*
		 * обработчик потдверждения включения опции автопубликации
		 * в случае отказа создается новое состояние 
		 * с данными созданной поездки
		 * для подтверждения
		 * (состояние нужно только для отправки сообщения пользователю)
		 */
		MessageHandler confirmingAutoPostOptionHandler = new MessageHandler() {

			@Override
			public int handle(SimpleMessenger messenger, MessageStandardClass message, State state) {
				ProcessingCenter pCenter = ProcessingCenter.getInstance();
				String userId = message.getUserId();
				Fleight fleight = pCenter.getIncompletedFleight(userId);
				
				if (!pCenter.isContainsFleight(userId)) {
					State errState = new ErrorState();
					errState.setMessage("Ошибка. Прошло слишком много времени или система была перезагружена. Нужно начать сначала.");
					pCenter.setState(messenger, userId, errState);
					return -1;
				}
				
				if (message.getText().equals("1")) {
					fleight.setAutoPostOn(true);
					if (fleight.isAutoPostExist()) {
						State err = new ErrorState().setMessage("Автопубликация в этом направлении уже активирована.").setPrevState(state).setNextHandler(new BackCommandHandler());
						fleight.setAutoPostOn(false);
						pCenter.setSavedState(userId, err);
						pCenter.setState(messenger, userId, err);
						return -1;
					}
					pCenter.setState(messenger, userId, state.get(1));
					return 1;
				} else if (message.getText().equals("2")) {
					
					StringBuilder result = new StringBuilder("Проверь и потверди.\n");
					result.append(fleight.getFullInfo());
					result.append("\n1 - Подтвердить");
					
					State postConfirmingState = new State("1.1.1.1.1.1.2", false) {
						
						@Override
						public String buildText() {
							return this.getMessage();
						}
						
					};
					
					postConfirmingState.setMessage(result.toString());
					pCenter.setSavedState(userId, postConfirmingState);
					pCenter.setState(messenger, userId, postConfirmingState);
					return 1;
				}
				
				return this.getNext().handle(messenger, message, state);
			}
			
		};
		
		/*
		 * обработчик ввода дней недели для автопубликации
		 * если введено корректно
		 * то создается состояние с данными поездки
		 * для дальнейшего подтверждения
		 */
		MessageHandler weekDaysCommandHandler = new MessageHandler () {

			@Override
			public int handle(SimpleMessenger messenger, MessageStandardClass message, State state) {
				String text = message.getText();
				String userId = message.getUserId();
				ProcessingCenter pCenter = ProcessingCenter.getInstance();
				
				if (!pCenter.isContainsFleight(userId)) {
					State errState = new ErrorState();
					errState.setMessage("Ошибка. Прошло слишком много времени или система была перезагружена. Нужно начать сначала.");
					pCenter.setState(messenger, userId, errState);
					return -1;
				}
				if (text.replaceAll("[([пП][нНтТ])([вВчЧ][тТ])([сС][рРбБ])([вВ][сС])[\\s]]", "").trim().equals("") ) {
					Fleight fleight = pCenter.getIncompletedFleight(userId);
					fleight.setAutoPostDays(text);
					
					StringBuilder result = new StringBuilder("Проверь и потверди.\n");
					result.append(fleight.getFullInfo());
					result.append("\n1 - Подтвердить");
					
					State postConfirmingState = new State("1.1.1.1.1.1.1.1.1", false) {
						@Override
						public String buildText() {
							return this.getMessage();
						}
					};
					
					postConfirmingState.setMessage(result.toString());
					pCenter.setSavedState(userId, postConfirmingState);
					pCenter.setState(messenger, userId, postConfirmingState);
					return 1;
				}
				return this.getNext().handle(messenger, message, state);
			}
			
		};
		
		/*
		 * обработчик подтверждения публикации
		 */
		MessageHandler postConfirmingHandler = new MessageHandler() {

			@Override
			public int handle(SimpleMessenger messenger, MessageStandardClass message, State state) {
				if (message.getText().equals("1")) {
					String userId = message.getUserId();
					ProcessingCenter pCenter = ProcessingCenter.getInstance();
					
					if (!pCenter.isContainsFleight(userId)) {
						State errState = new ErrorState();
						errState.setMessage("Ошибка. Прошло слишком много времени или система была перезагружена. Нужно начать сначала.");
						pCenter.setState(messenger, userId, errState);
						return -1;
					}
					
					pCenter.getIncompletedFleight(userId).post(messenger);
					pCenter.setState(messenger, userId, state.get(1));
					return 1;
				}
				return this.getNext().handle(messenger, message, state);
			}
			
		};
		
		
		NullState nullState = NullState.getInstance();
		MessageHandler commonHandler = ProcessingCenter.getInstance().getHandler();
		
		State mainMenu = new State("", "Главное меню. Выбери нужное, отправив соответствующее сообщение:", new OptionsListCommandHandler().setNext(new SelectMenuItemCommandHandler()).setNext(new UnknownCommandHandler()).setNext(new FastCreateFleightCommandHandler()).setNext(new FastFleightFindCommandHandler()), true);
		mainMenu.setName("Главное меню").setIsMainMenuButtonOn(false).setIsBackButtonOn(false);
		mainMenu.setNextHandler(new MessageHandler() {

			@Override
			public int handle(SimpleMessenger messenger, MessageStandardClass message, State state) {
				ProcessingCenter pCenter = ProcessingCenter.getInstance();
				pCenter.setState(messenger, message.getUserId(), state);
				return 1;
			}
			
		});
		nullState.addState(mainMenu);
		
			/*
			 * состояние с добавленными вариантами 
			 * для обработчика направления
			 */
			State state = new State("1", "Выбери направление, по которому едешь.", directionHandler.setNext(commonHandler), true) {
				
				@Override
				public String buildText() {
					try {
						return this.getMessage() + Fleight.getDirectionNames();
					} catch (Exception e) {
						
					}
					return "error";
				}
				
			};
			state.setName("Опубликовать поездку");
			nullState.addState(state);
			
				/*
				 * состояние с добавленными вариантами 
				 * для обработчика дня
				 */
				state = new State("1.1", "Выбери день, в который поедешь.", dayHandler.setNext(commonHandler), false) {
					@Override
					public String buildText() {
						return this.getMessage() + Fleight.getDayNames();
					}
				};
				state.setName("Направление");
				nullState.addState(state);
			
					state = new State("1.1.1", "Введи время выезда в формате ЧЧ:ММ.", new BackCommandHandler().setNext(new MainMenuCommandHandler()).setNext(new UnknownCommandHandler()).setNext(new TimeCommandHandler()), false);
					state.setName("День");
					nullState.addState(state);
					
						state = new State("1.1.1.1", "Введи номер телефона.", selectSavedNumberCommandHandler.setNext(new BackCommandHandler()).setNext(new MainMenuCommandHandler()).setNext(numbHandler), false);
						state.setName("Ввести время");
						nullState.addState(state);
						
							state = new State("1.1.1.1.1", "Введи заметку (для удобства можно написать откуда выезжаешь и как едешь).", selectSavedNoteCommandHandler.setNext(new MainMenuCommandHandler()), false);
							state.setName("Ввести номер телефона");
							state.setNextHandler(new BackCommandHandler() {
								@Override
								public int handle(SimpleMessenger messenger, MessageStandardClass message, State state) {
									if (message.getText().equals("0")) {
										final ProcessingCenter pCenter= ProcessingCenter.getInstance();
										final String userId = message.getUserId();
										pCenter.setState(messenger, userId, new State("1.1.1.1", false) {
											@Override
											public String buildText() {
												String text = "Введи номер телефона.";
												SelectSQLRequest request = new SelectSQLRequest("vk_bot", "user_ids", "root", pCenter.getUrl(), pCenter.getDriver(), pCenter.getPassFileName()).setWhereFields("user_id = " + userId).addSelectingField("saved_number");
												RowArray result = request.execute();
												if (result.next() && (result.getString("saved_number") != null)) {
													String number = result.getString("saved_number");
													text += "\nРанее использованный номер: " + number+ "\n1 - Использовать сохраненный номер";
												}
												return text;
											}
										});
										return 1;
									}
									else return getNext().handle(messenger, message, state);
								}
							}.setNext(noteHandler));
							nullState.addState(state);
							
								state = new State("1.1.1.1.1.1", "Установить автопубликацию?", new MainMenuCommandHandler().setNext(new UnknownCommandHandler()).setNext(confirmingAutoPostOptionHandler), false);
								state.setName("Ввести заметку");
								state.setNextHandler(new BackCommandHandler() {
									@Override
									public int handle(SimpleMessenger messenger, MessageStandardClass message, State state) {
										if (message.getText().equals("0")) {
											final ProcessingCenter pCenter= ProcessingCenter.getInstance();
											final String userId = message.getUserId();
											pCenter.setState(messenger, userId, new State("1.1.1.1.1", false) {
												@Override
												public String buildText() {
													String text = "Введи заметку (для удобства можно написать откуда выезжаешь и как едешь).";
													SelectSQLRequest request = new SelectSQLRequest("vk_bot", "user_ids", "root", pCenter.getUrl(), pCenter.getDriver(), pCenter.getPassFileName()).setWhereFields("user_id = " + userId).addSelectingField("saved_note");
													RowArray result = request.execute();
													if (result.next() && (result.getString("saved_note") != null)) {
														String note = result.getString("saved_note");
														text += "\nРанее использованная заметка: " + note + "\n1 - Использовать сохраненную заметку";
													}
													return text;
												}
											});
											return 1;
										}
										else return getNext().handle(messenger, message, state);
									}
								}.setNext(noteHandler));
								nullState.addState(state);
								
									state = new State("1.1.1.1.1.1.1", "Выбери нужный вариант. При выборе варианта \"На текущую неделю\" функция будет активирована до конца текущей недели, а не на следующие 7 дней.\n\nПримечание: если ты добавляешь автопубликацию на текующую неделю в воскресенье, то она переходит на следующую неделю. Сделано это для того, чтобы тебе не приходилось в понедельник лишний раз заходить и включать автопубликацию, если ты в воскресенье публикуешь на понедельник поездку и хочешь включить на следующую неделю опцию.", new BackCommandHandler().setNext(new MainMenuCommandHandler()).setNext(new UnknownCommandHandler()), true) {
										
										@Override
										public String buildText() {
											StringBuilder sb = new StringBuilder(this.getMessage());
											sb.append("\n1 - На текущую неделю\n2 - Еженедельно");
											int itemNumber = 1;
											for (int i = 1; i < this.getStatesArraySize(); i++) {
												if (this.get(i).isMenuItem()) {
													sb.append("\n" + itemNumber++ + " - ");
													sb.append(this.get(i).getName());
												}
											}
											return sb.toString();
										}
										
									};
									state.setName("Да");
									state.setNextHandler(new MessageHandler() {
										
										@Override
										public int handle(SimpleMessenger messenger, MessageStandardClass message, State state) {
											ProcessingCenter pCenter = ProcessingCenter.getInstance();
											String userId = message.getUserId();
											
											if (!pCenter.isContainsFleight(userId)) {
												State errState = new ErrorState();
												errState.setMessage("Ошибка. Прошло слишком много времени или система была перезагружена. Нужно начать сначала.").setIsBackButtonOn(false);
												pCenter.setState(messenger, userId, errState);
												return -1;
											}
											
											Fleight fleight = pCenter.getIncompletedFleight(userId);
											
											if (message.getText().equals("1")) {
												fleight.setFrequency(false);
												pCenter.setState(messenger, userId, state.get(1));
												return 1;
											} else if (message.getText().equals("2")) {
												fleight.setFrequency(true);
												pCenter.setState(messenger, userId, state.get(1));
												return 1;
											}
											
											return this.getNext().handle(messenger, message, state);
										}
										
									});
									nullState.addState(state);
									
										state = new State("1.1.1.1.1.1.1.1", "Введи через пробел необходимые дни недели для публикации. Для ввода нужно использовать только следующие сокращения: понедельник - пн, вторник - вт, среда - ср, четверг - чт, пятница - пт, суббота - сб, воскресенье - вс.\n\nПримечание: дни недели вводятся одним сообщением, в дальнейшем дополнить их пока невозможно.", new BackCommandHandler().setNext(new MainMenuCommandHandler()).setNext(new UnknownCommandHandler()), false);
										state.setName("Выбрать частоту");
										state.setNextHandler(weekDaysCommandHandler);
										nullState.addState(state);
										
											state = new State("1.1.1.1.1.1.1.1.1", "Проверь и подтверди публикацию.", new BackCommandHandler().setNext(new MainMenuCommandHandler()).setNext(new UnknownCommandHandler()), false);
											state.setName("Выбрать дни недели");
											state.setNextHandler(postConfirmingHandler);
											state.get(0).setHandler(new MessageHandler() {

												@Override
												public int handle(SimpleMessenger messenger,MessageStandardClass message, State state) {
													if (message.getText().equals("0")) {
														State nextState = ProcessingCenter.getInstance().getSavedState(message.getUserId());
														if (nextState != null) {
															ProcessingCenter.getInstance().setState(messenger, message.getUserId(), nextState);
															return 1;
														} else {
															ProcessingCenter.getInstance().setState(messenger, message.getUserId(), new ErrorState().setMessage("Прошло слишком много времени или система была перезагружена.").setIsBackButtonOn(false));
															return -1;
														}
													}
													return this.getNext().handle(messenger, message, state);
												}
												
											}).setNextHandler(new MainMenuCommandHandler()).setNextHandler(new UnknownCommandHandler());
											nullState.addState(state);
											
												state = new State("1.1.1.1.1.1.1.1.1.1", "Запись добавлена.\nP.S. помни - водитель в ответе за тех, кого сажает в машину. Пристегивайтесь ремнями безопасности.", new MainMenuCommandHandler().setNext(new UnknownCommandHandler()), true) {
													
													@Override
													public String buildText() {
														StringBuilder sb = new StringBuilder(this.getMessage());
														int itemNumber = 1;
														for (int i = 1; i < this.getStatesArraySize(); i++) {
															if (this.get(i).isMenuItem()) {
																sb.append("\n" + itemNumber++ + " - ");
																sb.append(this.get(i).getName());
															}
														}
														return sb.toString();
													}
													
												};
												state.setName("Подтвердить").setIsBackButtonOn(false);
												nullState.addState(state);
									
									state = new State("1.1.1.1.1.1.2", "Проверь и подтверди публикацию.", new BackCommandHandler().setNext(new MainMenuCommandHandler()).setNext(new UnknownCommandHandler()), true);
									state.setName("Нет");
									state.setNextHandler(postConfirmingHandler);
									state.get(0).setHandler(new MessageHandler() {

										@Override
										public int handle(SimpleMessenger messenger,MessageStandardClass message, State state) {
											if (message.getText().equals("0")) {
												State nextState = ProcessingCenter.getInstance().getSavedState(message.getUserId());
												if (nextState != null) {
													ProcessingCenter.getInstance().setState(messenger, message.getUserId(), nextState);
													return 1;
												} else {
													ProcessingCenter.getInstance().setState(messenger, message.getUserId(), new ErrorState().setMessage("Прошло слишком много времени или система была перезагружена.").setIsBackButtonOn(false));
													return -1;
												}
											}
											return this.getNext().handle(messenger, message, state);
										}
										
									}).setNextHandler(new MainMenuCommandHandler()).setNextHandler(new UnknownCommandHandler());
									nullState.addState(state);
									
										state = new State("1.1.1.1.1.1.2.1", "Запись добавлена.\nP.S. помни - водитель в ответе за тех, кого сажает в машину. Пристегивайтесь ремнями безопасности.", new MainMenuCommandHandler().setNext(new UnknownCommandHandler()), true) {
											
											@Override
											public String buildText() {
												StringBuilder sb = new StringBuilder(this.getMessage());
												int itemNumber = 1;
												for (int i = 1; i < this.getStatesArraySize(); i++) {
													if (this.get(i).isMenuItem()) {
														sb.append("\n" + itemNumber++ + " - ");
														sb.append(this.get(i).getName());
													}
												}
												return sb.toString();
											}
											
										};
										state.setName("Подтвердить").setIsBackButtonOn(false);
										nullState.addState(state);
					
					
			state = new State("2", "Выбери направление, по которому хочешь найти поездку.", directionHandler1.setNext(commonHandler), true) {
				
				@Override
				public String buildText() {
					try {
						return this.getMessage() + Fleight.getDirectionNames();
					} catch (Exception e) {
						
					}
					return "error";
				}
				
			};
			state.setName("Найти поездку");
			nullState.addState(state);
			
				state = new State("2.1", "Выбери день, в который хочешь поехать.", dayHandler1.setNext(commonHandler), false) {
					@Override
					public String buildText() {
						return this.getMessage() + Fleight.getDayNames();
					}
				};
				state.setName("Направление");
				nullState.addState(state);
			
					state = new State("2.1.1", "Введи желаемое время выезда в формате ЧЧ:ММ.", new BackCommandHandler().setNext(new MainMenuCommandHandler()).setNext(new UnknownCommandHandler()).setNext(new TimeCommandHandler()), false);
					state.setName("День");
					nullState.addState(state);
					
					
			state = new State("3", START_MESSAGE, new MainMenuCommandHandler().setNext(new UnknownCommandHandler()), true);
			state.setName("О боте").setIsBackButtonOn(false);
			nullState.addState(state);
			
			state = new State("4", "Включенные опции.", new MainMenuCommandHandler().setNext(new BackCommandHandler()).setNext(new UnknownCommandHandler()).setNext(new DeleteOptionCommandHandler()), true);
			state.setName("Включенные опции и опубликованные поездки");
			state.get(0).setHandler(new MessageHandler() {

				@Override
				public int handle(SimpleMessenger messenger,MessageStandardClass message, State state) {
					if (message.getText().equals("0")) {
						State nextState = ProcessingCenter.getInstance().getSavedState(message.getUserId());
						if (nextState != null) {
							ProcessingCenter.getInstance().setState(messenger, message.getUserId(), nextState);
							return 1;
						} else {
							ProcessingCenter.getInstance().setState(messenger, message.getUserId(), new ErrorState().setMessage("Прошло слишком много времени или система была перезагружена."));
							return -1;
						}
					}
					return this.getNext().handle(messenger, message, state);
				}
				
			}).setNextHandler(new MainMenuCommandHandler()).setNextHandler(new UnknownCommandHandler());
			nullState.addState(state);
		try {
			BufferedReader reader = new BufferedReader(new FileReader("S:/WorkSpace/vkbot/bott/conf.txt"));
			GroupActor actor = new GroupActor(189799593, reader.readLine());
			System.out.println(ProcessingCenter.getInstance());
			Bot bot = new Bot(vk, actor);
			Scheduler scheduler = new StdSchedulerFactory().getScheduler();
			Trigger dailyTrigger = TriggerBuilder.newTrigger().withIdentity("midnightTrigger", "group1").withSchedule(CronScheduleBuilder.cronSchedule("0 0 0 * * ? *")).build();
			JobDetail jDetail = JobBuilder.newJob(TablesUpdatingJob.class).withIdentity("tablesUpdating", "myFirstGroup").build();
			scheduler.start();
			scheduler.scheduleJob(jDetail, dailyTrigger);
			reader.close();
			bot.uploadGroupActors("conf.txt");
			bot.run();
		} catch (Exception e) {
			System.out.println(e);
		} 

	}

}
